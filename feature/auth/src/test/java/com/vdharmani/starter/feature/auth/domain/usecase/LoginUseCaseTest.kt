package com.vdharmani.starter.feature.auth.domain.usecase

import com.vdharmani.starter.feature.auth.domain.EmptyFieldException
import com.vdharmani.starter.feature.auth.domain.InvalidEmailException
import com.vdharmani.starter.feature.auth.domain.WeakPasswordException
import com.vdharmani.starter.feature.auth.domain.model.AuthState
import com.vdharmani.starter.feature.auth.domain.model.AuthToken
import com.vdharmani.starter.feature.auth.domain.model.ResetToken
import com.vdharmani.starter.feature.auth.domain.model.User
import com.vdharmani.starter.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Validates the input checks in [LoginUseCase] and confirms it delegates to
 * the repository for valid input. Reference pattern for testing use cases —
 * no DI, no Android, plain JUnit + a hand-rolled fake repository.
 */
class LoginUseCaseTest {

    @Test
    fun `blank email returns EmptyFieldException`() = runTest {
        val sut = LoginUseCase(repository = FakeAuthRepository())
        val result = sut(email = "", password = "password123")
        assertTrue(result.exceptionOrNull() is EmptyFieldException)
    }

    @Test
    fun `email without @ returns InvalidEmailException`() = runTest {
        val sut = LoginUseCase(repository = FakeAuthRepository())
        val result = sut(email = "notanemail", password = "password123")
        assertTrue(result.exceptionOrNull() is InvalidEmailException)
    }

    @Test
    fun `password shorter than 8 chars returns WeakPasswordException`() = runTest {
        val sut = LoginUseCase(repository = FakeAuthRepository())
        val result = sut(email = "foo@bar.com", password = "short")
        assertTrue(result.exceptionOrNull() is WeakPasswordException)
    }

    @Test
    fun `valid inputs trim email and delegate to repository`() = runTest {
        val fake = FakeAuthRepository(loginResult = Result.success(AuthToken("acc", "ref")))
        val sut = LoginUseCase(repository = fake)

        val result = sut(email = "  foo@bar.com  ", password = "password123")

        assertTrue(result.isSuccess)
        assertEquals(AuthToken("acc", "ref"), result.getOrThrow())
        assertEquals("foo@bar.com", fake.lastEmail)
        assertEquals("password123", fake.lastPassword)
    }

    @Test
    fun `repository failure propagates`() = runTest {
        val failure = Result.failure<AuthToken>(IllegalStateException("server is down"))
        val sut = LoginUseCase(repository = FakeAuthRepository(loginResult = failure))

        val result = sut(email = "foo@bar.com", password = "password123")

        assertTrue(result.isFailure)
        assertEquals("server is down", result.exceptionOrNull()?.message)
    }
}

/**
 * Minimal hand-rolled fake — preferred over a mocking library for use case
 * tests because the surface is small and the assertions are clearer.
 */
private class FakeAuthRepository(
    private val loginResult: Result<AuthToken> =
        Result.success(AuthToken("default-access", "default-refresh")),
) : AuthRepository {

    var lastEmail: String? = null
        private set
    var lastPassword: String? = null
        private set

    override suspend fun login(email: String, password: String): Result<AuthToken> {
        lastEmail = email
        lastPassword = password
        return loginResult
    }

    override suspend fun signup(email: String, password: String, name: String): Result<AuthToken> =
        Result.failure(NotImplementedError())

    override suspend fun forgotPassword(email: String): Result<Unit> =
        Result.failure(NotImplementedError())

    override suspend fun verifyOtp(email: String, otp: String): Result<ResetToken> =
        Result.failure(NotImplementedError())

    override suspend fun resetPassword(token: ResetToken, newPassword: String): Result<Unit> =
        Result.failure(NotImplementedError())

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> =
        Result.failure(NotImplementedError())

    override suspend fun logout(): Result<Unit> = Result.failure(NotImplementedError())

    override suspend fun deleteAccount(): Result<Unit> = Result.failure(NotImplementedError())

    override fun observeAuthState(): Flow<AuthState> = flowOf(AuthState.SignedOut)

    override suspend fun currentUser(): User? = null
}

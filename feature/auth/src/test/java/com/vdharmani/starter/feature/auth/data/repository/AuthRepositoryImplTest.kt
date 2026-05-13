package com.vdharmani.starter.feature.auth.data.repository

import com.vdharmani.starter.core.common.HttpException
import com.vdharmani.starter.core.common.NetworkException
import com.vdharmani.starter.core.common.UnauthorizedException
import com.vdharmani.starter.core.network.apiCall
import com.vdharmani.starter.feature.auth.data.remote.AuthApi
import com.vdharmani.starter.feature.auth.data.remote.dto.LoginRequestDto
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Exercises `core:network`'s [apiCall] error mapping using a real Retrofit +
 * [MockWebServer]. This is the cheapest test that meaningfully covers the
 * network plumbing — no Hilt, no Robolectric, no `core:datastore` Context
 * dance.
 *
 * Reference pattern for repository tests:
 *   - Real Retrofit + real OkHttp + real serialization (catches DTO bugs).
 *   - Real `apiCall { }` (catches mapping regressions).
 *   - MockWebServer to script HTTP responses.
 *
 * The full repo test (asserting TokenStore/UserDao side-effects) requires
 * either Robolectric or an extracted TokenStore interface — out of scope
 * for the starter template; junior adds it when they introduce a feature
 * test stack.
 */
class AuthRepositoryImplTest {

    private lateinit var server: MockWebServer
    private lateinit var api: AuthApi

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        val json = Json { ignoreUnknownKeys = true; isLenient = true; explicitNulls = false }
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        api = retrofit.create(AuthApi::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `200 response returns Result_success with token`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"token":"server-issued-token"}"""),
        )

        val result = apiCall { api.login(LoginRequestDto("eve.holt@reqres.in", "cityslicka")) }

        assertTrue(result.isSuccess)
        assertEquals("server-issued-token", result.getOrThrow().token)
    }

    @Test
    fun `401 response maps to UnauthorizedException`() = runTest {
        server.enqueue(MockResponse().setResponseCode(401).setBody("{}"))

        val result = apiCall { api.login(LoginRequestDto("eve.holt@reqres.in", "wrong")) }

        assertTrue(
            "expected UnauthorizedException but got ${result.exceptionOrNull()}",
            result.exceptionOrNull() is UnauthorizedException,
        )
    }

    @Test
    fun `500 response maps to HttpException preserving the status code`() = runTest {
        server.enqueue(MockResponse().setResponseCode(500).setBody("{}"))

        val result = apiCall { api.login(LoginRequestDto("eve.holt@reqres.in", "cityslicka")) }

        val ex = result.exceptionOrNull()
        assertTrue(ex is HttpException)
        assertEquals(500, (ex as HttpException).code)
    }

    @Test
    fun `socket failure maps to NetworkException`() = runTest {
        server.shutdown() // server unreachable

        val result = apiCall { api.login(LoginRequestDto("eve.holt@reqres.in", "cityslicka")) }

        assertTrue(
            "expected NetworkException but got ${result.exceptionOrNull()}",
            result.exceptionOrNull() is NetworkException,
        )
    }
}

package com.vdharmani.starter.feature.auth.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdharmani.starter.feature.auth.domain.usecase.SignupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val signup: SignupUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SignupUiState())
    val state: StateFlow<SignupUiState> = _state.asStateFlow()

    private val _effects = Channel<SignupEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val effects = _effects.receiveAsFlow()

    fun handle(intent: SignupIntent) {
        when (intent) {
            is SignupIntent.NameChanged ->
                _state.update { it.copy(name = intent.value, nameError = null) }

            is SignupIntent.EmailChanged ->
                _state.update { it.copy(email = intent.value, emailError = null) }

            is SignupIntent.PasswordChanged ->
                _state.update { it.copy(password = intent.value, passwordError = null) }

            SignupIntent.Submit -> submit()
            SignupIntent.BackToLogin -> { /* handled in screen */ }
        }
    }

    private fun submit() {
        if (state.value.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            signup(state.value.email, state.value.password, state.value.name)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _effects.trySend(SignupEffect.NavigateToHome)
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false) }
                    _effects.trySend(SignupEffect.ShowError(e.message ?: "Sign-up failed"))
                }
        }
    }
}

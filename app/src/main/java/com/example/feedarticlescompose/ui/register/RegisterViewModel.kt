package com.example.feedarticlescompose.ui.register

import ERROR_403
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlescompose.dataclass.RegisterDto
import com.example.feedarticlescompose.dataclass.SessionDto
import com.example.feedarticlescompose.network.ApiService
import com.example.feedarticlescompose.ui.login.LoginViewModel
import com.example.feedarticlescompose.utils.MySharedPref
import com.example.feedarticlescompose.utils.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sharedPref: MySharedPref
): ViewModel() {

    enum class RegisterState {
        ERROR_AUTHORIZATION,
        ERROR_CONNECTION,
        ERROR_SERVER,
        EMPTY_FIELDS,
        ERROR_CONFIRMATION
    }

    private val _loginStateFlow = MutableStateFlow("")
    val loginStateFlow = _loginStateFlow.asStateFlow()

    private val _passwordStateFlow = MutableStateFlow("")
    val passwordStateFlow = _passwordStateFlow.asStateFlow()

    private val _confirmStateFlow = MutableStateFlow("")
    val confirmStateFlow = _confirmStateFlow.asStateFlow()

    private val _messageSharedFlow = MutableSharedFlow<RegisterState>()
    val messageSharedFlow = _messageSharedFlow.asSharedFlow()

    private val _goToMainSharedFlow = MutableSharedFlow<Screen>()
    val goToMainSharedFlow = _goToMainSharedFlow.asSharedFlow()

    private var message: RegisterState? = null


    fun updateLogin(login: String) {
        _loginStateFlow.value = login
    }
    fun updatePassword(password: String) {
        _passwordStateFlow.value = password
    }
    fun updateConfirm(confirm: String) {
        _confirmStateFlow.value = confirm
    }

    fun register() {
        if (
            loginStateFlow.value.isNotBlank()
            &&
            passwordStateFlow.value.isNotBlank()
        ) {
            if(passwordStateFlow.value != confirmStateFlow.value) {
                try {

                    viewModelScope.launch {
                        val responseRegister: Response<SessionDto>? = withContext(Dispatchers.IO) {
                            apiService.register(RegisterDto(loginStateFlow.value, passwordStateFlow.value))
                        }

                        val body = responseRegister?.body()

                        when {
                            responseRegister?.body() == null ->
                                message = RegisterState.ERROR_SERVER

                            responseRegister.isSuccessful && (body != null) -> {
                                sharedPref.saveToken(body.token ?: "")
                                sharedPref.saveUserId(body.id)
                                _goToMainSharedFlow.emit(Screen.Main)
                            }


                            responseRegister.code() == ERROR_403 ->
                                message = RegisterState.ERROR_AUTHORIZATION
                        }
                    }

                } catch (e: Exception) {
                    message = RegisterState.ERROR_CONNECTION
                }
            } else {
                message = RegisterState.ERROR_CONFIRMATION
            }
        } else {
            message = RegisterState.EMPTY_FIELDS
        }

        message?.let {
            viewModelScope.launch {
                _messageSharedFlow.emit(it)
            }
        }
    }
}

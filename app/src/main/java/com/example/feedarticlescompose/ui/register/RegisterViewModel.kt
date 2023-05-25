package com.example.feedarticlescompose.ui.register

import ERROR_400
import ERROR_503
import HTTP_200
import HTTP_303
import HTTP_304
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
import com.example.feedarticlescompose.utils.Result

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sharedPref: MySharedPref
): ViewModel() {

    enum class RegisterState(val httpStatus: Int?) {
        ERROR_SERVICE(ERROR_503),
        ERROR_CONNECTION(null),
        ERROR_SERVER(null),
        EMPTY_FIELDS(null),
        ERROR_CONFIRMATION(null),
        ERROR_PARAM(ERROR_400),
        LOGIN_USED(HTTP_303),
        SUCCESS(HTTP_200),
        FAILURE(HTTP_304);

        companion object {
            fun getState(httpStatus: Int): RegisterState? {
                RegisterState.values().forEach { state ->
                    if (state.httpStatus == httpStatus) {
                        return state
                    }
                }
                return null
            }
        }
    }


    private val _loginStateFlow = MutableStateFlow("")
    val loginStateFlow = _loginStateFlow.asStateFlow()

    private val _passwordStateFlow = MutableStateFlow("")
    val passwordStateFlow = _passwordStateFlow.asStateFlow()

    private val _confirmStateFlow = MutableStateFlow("")
    val confirmStateFlow = _confirmStateFlow.asStateFlow()

    private val _registerStateSharedFlow = MutableSharedFlow<RegisterState>()
    val registerStateSharedFlow = _registerStateSharedFlow.asSharedFlow()

    private val _goToMainSharedFlow = MutableSharedFlow<Screen>()
    val goToMainSharedFlow = _goToMainSharedFlow.asSharedFlow()


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
            if (passwordStateFlow.value == confirmStateFlow.value) {

                try {

                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            val responseRegister = apiService.register(
                                RegisterDto(loginStateFlow.value, passwordStateFlow.value)
                            )

                            if(responseRegister?.isSuccessful == true ) {

                                Result.Success(
                                    responseRegister.body(),
                                    responseRegister.code()
                                ).let {result->
                                    sharedPref.saveToken(result.data?.token ?: "")
                                    sharedPref.saveUserId(result.data?.id ?: 0)
                                    _goToMainSharedFlow.emit(Screen.Main)

                                    RegisterState.getState(result.httpStatus)
                                        ?.let { state->
                                            _registerStateSharedFlow.emit(state)
                                        }
                                }

                            } else {
                                Result.HttpStatus(responseRegister?.code() ?: 0)
                                    .let { result->
                                        RegisterState.getState(result.httpStatus)?.let { state->
                                            _registerStateSharedFlow.emit(state)
                                        }
                                    }
                            }
                        }
                    }

                } catch (e: Exception) {
                    Result.ExeptionError(
                        RegisterState.ERROR_CONNECTION
                    ).let {
                        viewModelScope.launch {
                            _registerStateSharedFlow.emit(it.state)
                        }
                    }

                }
            } else
                Result.Failure(
                    RegisterState.ERROR_CONFIRMATION
                ).let {
                    viewModelScope.launch {
                        _registerStateSharedFlow.emit(it.state)
                    }
                }

        } else
            Result.Failure(
                RegisterState.EMPTY_FIELDS
            ).let {
                viewModelScope.launch {
                    _registerStateSharedFlow.emit(it.state)
                }
            }

    }
}

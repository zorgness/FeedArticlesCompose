package com.example.feedarticlescompose.ui.login

import ERROR_400
import ERROR_401
import ERROR_503
import HTTP_200
import HTTP_304
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlescompose.dataclass.SessionDto
import com.example.feedarticlescompose.network.ApiService
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
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sharedPref: MySharedPref

) : ViewModel() {

    enum class LoginState {
        SECURITY_FAILURE,
        ERROR_PARAM,
        ERROR_CONNECTION,
        ERROR_SERVER,
        WRONG_CREDENTIAL,
        EMPTY_FIELDS,
        ERROR_SERVICE
    }

    enum class LoginErrorState (val httpStatus: Int?) {
        SECURITY_FAILURE(HTTP_304),
        ERROR_PARAM(ERROR_400),
        WRONG_CREDENTIAL(ERROR_401),
        ERROR_SERVICE(ERROR_503)
    }


    private val _loginStateFlow = MutableStateFlow("")
    val loginStateFlow = _loginStateFlow.asStateFlow()

    private val _passwordStateFlow = MutableStateFlow("")
    val passwordStateFlow = _passwordStateFlow.asStateFlow()

    private val _loginStateSharedFlow = MutableSharedFlow<LoginState>()
    val loginStateSharedFlow = _loginStateSharedFlow.asSharedFlow()

    private val _goToMainSharedFlow = MutableSharedFlow<Screen>()
    val goToMainSharedFlow = _goToMainSharedFlow.asSharedFlow()

    fun updateLogin(login: String) {
        _loginStateFlow.value = login
    }

    fun updatePassword(password: String) {
        _passwordStateFlow.value = password
    }

    fun login() {
        if (
            loginStateFlow.value.isNotBlank()
            &&
            passwordStateFlow.value.isNotBlank()
        ) {
            try {

                viewModelScope.launch {
                    val responseLogin: Response<SessionDto>? = withContext(Dispatchers.IO) {
                        apiService.login(loginStateFlow.value, passwordStateFlow.value)
                    }

                    val session = responseLogin?.body()

                    when {
                        responseLogin == null ->
                           _loginStateSharedFlow.emit(LoginState.ERROR_SERVER)

                        responseLogin.isSuccessful && (session != null) -> {
                            sharedPref.saveToken(session.token ?: "")
                            sharedPref.saveUserId(session.id)
                            _goToMainSharedFlow.emit(Screen.Main)
                        }
                    }


                    when(responseLogin?.code()) {
                        HTTP_304 -> LoginState.SECURITY_FAILURE
                        ERROR_400 -> LoginState.ERROR_PARAM
                        ERROR_401 -> LoginState.WRONG_CREDENTIAL
                        ERROR_503 -> LoginState.ERROR_SERVICE
                        else -> null
                    }?.let {
                        _loginStateSharedFlow.emit(it)
                    }
                }

            } catch (e: Exception) {
                viewModelScope.launch {
                    _loginStateSharedFlow.emit(LoginState.ERROR_CONNECTION)
                }
            }
        } else {
            viewModelScope.launch {
                _loginStateSharedFlow.emit(LoginState.EMPTY_FIELDS)
            }
        }


    }

}
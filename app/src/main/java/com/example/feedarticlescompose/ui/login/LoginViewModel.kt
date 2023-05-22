package com.example.feedarticlescompose.ui.login

import ERROR_401
import ERROR_403
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlescompose.dataclass.SessionDto
import com.example.feedarticlescompose.network.ApiService
import com.example.feedarticlescompose.ui.main.MainViewModel
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
        ERROR_AUTHORIZATION,
        ERROR_CONNECTION,
        ERROR_SERVER,
        WRONG_CREDENTIAL,
        EMPTY_FIELDS,
    }

    private val _loginStateFlow = MutableStateFlow("")
    val loginStateFlow = _loginStateFlow.asStateFlow()

    private val _passwordStateFlow = MutableStateFlow("")
    val passwordStateFlow = _passwordStateFlow.asStateFlow()

    private val _messageSharedFlow = MutableSharedFlow<LoginState>()
    val messageSharedFlow = _messageSharedFlow.asSharedFlow()

    private val _goToMainSharedFlow = MutableSharedFlow<Screen>()
    val goToMainSharedFlow = _goToMainSharedFlow.asSharedFlow()

    private var message: LoginState? = null


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

                    val body = responseLogin?.body()

                    when {
                        responseLogin?.body() == null ->
                            message = LoginState.ERROR_SERVER

                        responseLogin.isSuccessful && (body != null) -> {
                            sharedPref.saveToken(body.token ?: "")
                            sharedPref.saveUserId(body.id)
                            _goToMainSharedFlow.emit(Screen.Main)
                        }

                        responseLogin.code() == ERROR_401 ->
                            message = LoginState.WRONG_CREDENTIAL

                        responseLogin.code() == ERROR_403 ->
                            message = LoginState.ERROR_AUTHORIZATION
                    }
                }

            } catch (e: Exception) {
                message = LoginState.ERROR_CONNECTION
            }
        } else {
            message = LoginState.EMPTY_FIELDS
        }

        message?.let {
            viewModelScope.launch {
                _messageSharedFlow.emit(it)
            }
        }
    }
}
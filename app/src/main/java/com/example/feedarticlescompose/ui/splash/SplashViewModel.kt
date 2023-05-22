package com.example.feedarticlescompose.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlescompose.utils.MySharedPref
import com.example.feedarticlescompose.utils.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sharedPref: MySharedPref
): ViewModel() {

    private val _goToScreen = MutableSharedFlow<Screen>()
    val goToScreen = _goToScreen.asSharedFlow()

    fun initSplash() {
        viewModelScope.launch {
            delay(2000)
            sharedPref.getToken()?.let {
                _goToScreen.emit(Screen.Main)
            }
            ?:  _goToScreen.emit(Screen.Login)

        }

    }
}
package com.example.feedarticlescompose.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(): ViewModel() {

    private val _goToMainScreen = MutableSharedFlow<Boolean>()
    val goToMainScreen = _goToMainScreen.asSharedFlow()


    fun initSplash() {
        viewModelScope.launch {
            delay(2000)
            _goToMainScreen.emit(true)
        }

    }
}
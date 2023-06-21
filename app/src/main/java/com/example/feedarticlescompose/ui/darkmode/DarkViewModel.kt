package com.example.feedarticlescompose.ui.darkmode

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DarkViewModel @Inject constructor() : ViewModel() {

    private val _isDarkModelStateflow = MutableStateFlow(false)
    val isDarkMode = _isDarkModelStateflow.asStateFlow()

    fun setDarkMode() {
        _isDarkModelStateflow.value = !_isDarkModelStateflow.value
    }
}
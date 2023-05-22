package com.example.feedarticlescompose.ui.main

import ERROR_400
import ERROR_403
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlescompose.dataclass.ArticleDto
import com.example.feedarticlescompose.network.ApiService
import com.example.feedarticlescompose.utils.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiService: ApiService
): ViewModel() {

    enum class MainState {
        ERROR_SERVER,
        ERROR_CONNECTIOIN,
        ERROR_AUTHORIZATION,
        ERROR_PARAM
    }


    private val _messageSharedFlow = MutableSharedFlow<MainState>()
    val messageSharedFlow = _messageSharedFlow.asSharedFlow()

    private val _isLoadingStateFlow = MutableStateFlow(true)
    val isLoadingStateFlow = _isLoadingStateFlow.asStateFlow()

    private val _selectedCategoryStateflow = MutableStateFlow<Int>(0)
    val selectedCategoryStateflow = _selectedCategoryStateflow.asStateFlow()

    private val _articlesListStateFlow = MutableStateFlow(emptyList<ArticleDto>())
    val articlesListStateFlow = _articlesListStateFlow.asStateFlow()


    private var message: MainState? = null

    fun fetchArticles() {
            viewModelScope.launch {
                try {
                    val responseFetchAll: Response<List<ArticleDto>>? = withContext(Dispatchers.IO) {
                        apiService.fetchArticles()
                    }
                    val body = responseFetchAll?.body()

                    when {
                        responseFetchAll?.body() == null ->
                            message = MainState.ERROR_SERVER

                        responseFetchAll.isSuccessful && (body != null) -> {
                            //articlesFullList.addAll(body)
                            //if selected > 0 filter
                        }

                        responseFetchAll.code() == ERROR_403 ->
                            message = MainState.ERROR_AUTHORIZATION

                        responseFetchAll.code() == ERROR_400 ->
                            message = MainState.ERROR_PARAM
                    }
                } catch (e: Exception) {
                    message = MainState.ERROR_CONNECTIOIN
                }
            }


        message?.let {
            viewModelScope.launch {
                _messageSharedFlow.emit(it)
            }
        }

    }
}
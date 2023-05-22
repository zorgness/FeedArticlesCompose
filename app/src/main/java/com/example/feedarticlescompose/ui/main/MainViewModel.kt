package com.example.feedarticlescompose.ui.main

import ERROR_400
import ERROR_403
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlescompose.dataclass.ArticleDto
import com.example.feedarticlescompose.dataclass.GetArticlesDto
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
class MainViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sharedPref: MySharedPref
): ViewModel() {

    enum class MainState {
        ERROR_SERVER,
        ERROR_CONNECTION,
        ERROR_AUTHORIZATION,
        ERROR_PARAM
    }


    private val _isLoadingStateFlow = MutableStateFlow(true)
    val isLoadingStateFlow = _isLoadingStateFlow.asStateFlow()

    private val _selectedCategoryStateflow = MutableStateFlow<Int>(0)
    val selectedCategoryStateflow = _selectedCategoryStateflow.asStateFlow()

    private val _articlesListStateFlow = MutableStateFlow(emptyList<ArticleDto>())
    val articlesListStateFlow = _articlesListStateFlow.asStateFlow()

    private val _messageSharedFlow = MutableSharedFlow<MainState>()
    val messageSharedFlow = _messageSharedFlow.asSharedFlow()

    private val _goToLoginSharedFlow = MutableSharedFlow<Screen>()
    val goToLoginSharedFlow = _goToLoginSharedFlow.asSharedFlow()


    private var message: MainState? = null

    fun updateSelectedCategory(position: Int) {
        _selectedCategoryStateflow.value = position
        fetchAllOrFilteredArticles()
       // filterListArticle()
    }


    /* fun filterListArticle() {

        if(selectedCategoryStateflow.value > 0) {
            _articlesListStateFlow.value = body.articles.filter {article->
                article.categorie == selectedCategoryStateflow.value
            }
        } else {
            _articlesListStateFlow.value = body.articles
        }

    }*/

    fun fetchAllOrFilteredArticles() {

            val headers = HashMap<String, String>()
            headers["token"] = sharedPref.getToken() ?: ""

            viewModelScope.launch {
                try {
                    val responseFetchArticles: Response<GetArticlesDto>? = withContext(Dispatchers.IO) {
                        apiService.fetchAllArticles(headers)
                    }
                    val body = responseFetchArticles?.body()

                    when {
                        responseFetchArticles?.body() == null ->
                            message = MainState.ERROR_SERVER

                        responseFetchArticles.isSuccessful && (body != null) -> {

                            if(body.status == "ok") {

                                _articlesListStateFlow.value = body.articles
                                if(selectedCategoryStateflow.value > 0) {
                                    _articlesListStateFlow.value = body.articles.filter {article->
                                        article.categorie == selectedCategoryStateflow.value
                                    }
                                } else {
                                    _articlesListStateFlow.value = body.articles
                                }
                                _isLoadingStateFlow.value = false

                            }

                            if(body.status.contains("error"))
                                message = MainState.ERROR_PARAM
                        }

                        responseFetchArticles.code() == ERROR_403 ->
                            message = MainState.ERROR_AUTHORIZATION

                        responseFetchArticles.code() == ERROR_400 ->
                            message = MainState.ERROR_PARAM
                    }
                } catch (e: Exception) {
                    message = MainState.ERROR_CONNECTION
                }
            }


        message?.let {
            viewModelScope.launch {
                _messageSharedFlow.emit(it)
            }
        }

    }

    fun logout() {
        sharedPref.clearSharedPref()
        viewModelScope.launch {
            _goToLoginSharedFlow.emit(Screen.Login)
        }
    }
}
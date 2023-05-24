package com.example.feedarticlescompose.ui.main

import ERROR_400
import ERROR_403
import STATUS_REQUEST_ERROR
import STATUS_REQUEST_OK
import USER_TOKEN
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlescompose.dataclass.ArticleDto
import com.example.feedarticlescompose.dataclass.GetArticlesDto
import com.example.feedarticlescompose.network.ApiService
import com.example.feedarticlescompose.utils.MySharedPref
import com.example.feedarticlescompose.utils.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    private val _currentUserIdStateflow = MutableStateFlow(sharedPref.getUserId())
    val currentUserIdStateflow = _currentUserIdStateflow.asStateFlow()

    private val _isLoadingStateFlow = MutableStateFlow(true)
    val isLoadingStateFlow = _isLoadingStateFlow.asStateFlow()

    private val _isRefreshingStateFlow = MutableStateFlow(false)
    val isRefreshingStateFlow = _isRefreshingStateFlow.asStateFlow()

    private val _expandedIdStateFlow = MutableStateFlow(0L)
    val expandedIdStateFlow = _expandedIdStateFlow.asStateFlow()

    private val _selectedCategoryStateflow = MutableStateFlow<Int>(0)
    val selectedCategoryStateflow = _selectedCategoryStateflow.asStateFlow()

    private val _articlesToShowStateFlow = MutableStateFlow(emptyList<ArticleDto>())
    val articlesToShowStateFlow = _articlesToShowStateFlow.asStateFlow()

    private val _messageSharedFlow = MutableSharedFlow<MainState>()
    val messageSharedFlow = _messageSharedFlow.asSharedFlow()

    private val _goToLoginSharedFlow = MutableSharedFlow<Screen>()
    val goToLoginSharedFlow = _goToLoginSharedFlow.asSharedFlow()

    private val _goToEditSharedFlow = MutableSharedFlow<String>()
    val goToEditSharedFlow = _goToEditSharedFlow.asSharedFlow()

    private var articlesFullList = emptyList<ArticleDto>()

    private var message: MainState? = null

    fun updateSelectedCategory(position: Int) {
        _selectedCategoryStateflow.value = position
        fetchArticlesListToShow()
    }

    fun updateItemClicked(item: ArticleDto) {
        if(item.idU == sharedPref.getUserId()) {
            viewModelScope.launch {
                _goToEditSharedFlow.emit(Screen.Edit.route + "/${item.id}")
            }
        } else
            _expandedIdStateFlow.value = item.id
    }

    fun resetExpandedId() {
        _expandedIdStateFlow.value = 0L
    }

    fun setRefresh() {
        _isRefreshingStateFlow.value = !_isRefreshingStateFlow.value
        fetchAllArticles()
    }


    private fun fetchArticlesListToShow() {

        if(selectedCategoryStateflow.value > 0) {
            _articlesToShowStateFlow.value = articlesFullList.filter { article->
                article.categorie == selectedCategoryStateflow.value
            }
        } else
            _articlesToShowStateFlow.value = articlesFullList

    }

    fun fetchAllArticles() {

            val headers = HashMap<String, String>()
            headers[USER_TOKEN] = sharedPref.getToken() ?: ""

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

                            if(body.status == STATUS_REQUEST_OK) {
                                articlesFullList = body.articles
                                _isLoadingStateFlow.value = false
                                fetchArticlesListToShow()
                                delay(500)
                                _isRefreshingStateFlow.value = false

                            }

                            if(body.status.contains(STATUS_REQUEST_ERROR))
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
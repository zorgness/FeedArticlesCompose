package com.example.feedarticlescompose.ui.main

import ERROR_400
import ERROR_401
import ERROR_503
import HTTP_201
import HTTP_304
import USER_TOKEN
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlescompose.dataclass.ArticleDto
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
        ERROR_SERVICE,
        ERROR_SERVER,
        ERROR_CONNECTION,
        ERROR_AUTHORIZATION,
        ERROR_PARAM
    }

    enum class DeleteState {
        SUCCESS,
        FAILURE,
        ERROR_SERVICE,
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

    private val _mainStateSharedFlow = MutableSharedFlow<MainState>()
    val mainStateSharedFlow = _mainStateSharedFlow.asSharedFlow()

    private val _deleteStateSharedFlow = MutableSharedFlow<DeleteState>()
    val deleteStateSharedFlow = _deleteStateSharedFlow.asSharedFlow()

    private val _goToLoginSharedFlow = MutableSharedFlow<Screen>()
    val goToLoginSharedFlow = _goToLoginSharedFlow.asSharedFlow()

    private val _goToEditSharedFlow = MutableSharedFlow<String>()
    val goToEditSharedFlow = _goToEditSharedFlow.asSharedFlow()

    private var articlesFullList = emptyList<ArticleDto>()

    private var mainState: MainState? = null

    private var deleteState: DeleteState? = null

    private val headers = HashMap<String, String>()

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


            headers[USER_TOKEN] = sharedPref.getToken() ?: ""

            viewModelScope.launch {
                try {
                    val responseFetchArticles: Response<List<ArticleDto>>? = withContext(Dispatchers.IO) {
                        apiService.fetchAllArticles(headers)
                    }
                    val body = responseFetchArticles?.body()

                    when {
                        responseFetchArticles == null ->
                            mainState = MainState.ERROR_SERVER

                        responseFetchArticles.isSuccessful && (body != null) -> {
                            articlesFullList = body
                            _isLoadingStateFlow.value = false
                            fetchArticlesListToShow()
                            delay(500)
                            _isRefreshingStateFlow.value = false
                        }
                    }


                    when(responseFetchArticles?.code()) {
                        ERROR_400 -> MainState.ERROR_PARAM
                        ERROR_401 -> MainState.ERROR_AUTHORIZATION
                        ERROR_503 -> MainState.ERROR_SERVICE
                        else -> null
                    }.let {
                        mainState = it
                    }


                } catch (e: Exception) {
                    mainState = MainState.ERROR_CONNECTION
                }
            }


            mainState?.let {
                viewModelScope.launch {
                    _mainStateSharedFlow.emit(it)
                }
            }
    }


    fun deleteArticle(articleId: Long) {

        headers[USER_TOKEN] = sharedPref.getToken() ?: ""

        try {
            viewModelScope.launch {

                val responseDeleteArticle: Response<Unit>? = withContext(Dispatchers.IO) {
                    apiService.deleteArticle(articleId, headers)
                }

                when  {
                    responseDeleteArticle == null  ->
                        deleteState = DeleteState.ERROR_SERVER

                    responseDeleteArticle.isSuccessful ->
                        fetchAllArticles()
                }



                when(responseDeleteArticle?.code()) {
                    HTTP_201 -> DeleteState.SUCCESS
                    HTTP_304 -> DeleteState.FAILURE
                    ERROR_400 -> DeleteState.ERROR_PARAM
                    ERROR_401 -> DeleteState.ERROR_PARAM
                    ERROR_503 -> DeleteState.ERROR_SERVICE
                    else -> null
                }.let {
                    deleteState = it
                }

            }

        } catch (e: Exception) {
            deleteState = DeleteState.ERROR_CONNECTION
        }

        deleteState?.let {
            viewModelScope.launch {
                _deleteStateSharedFlow.emit(it)
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
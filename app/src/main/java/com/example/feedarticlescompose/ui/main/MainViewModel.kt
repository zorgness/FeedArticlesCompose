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
import javax.inject.Inject
import com.example.feedarticlescompose.utils.Result

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sharedPref: MySharedPref
): ViewModel() {

    enum class MainState(val httpStatus: Int?){
        ERROR_SERVICE(ERROR_503),
        ERROR_SERVER(null),
        ERROR_CONNECTION(null),
        ERROR_AUTHORIZATION(ERROR_401),
        ERROR_PARAM(ERROR_400);

        companion object {
            fun getCurrentState(httpStatus: Int): MainState? {
                MainState.values().forEach { state ->
                    if (state.httpStatus == httpStatus) {
                        return state
                    }
                }
                return null
            }
        }
    }

    enum class DeleteState(val httpStatus: Int?) {
        SUCCESS(HTTP_201),
        FAILURE(HTTP_304),
        ERROR_SERVICE(ERROR_503),
        ERROR_SERVER(null),
        ERROR_CONNECTION(null),
        ERROR_AUTHORIZATION(ERROR_401),
        ERROR_PARAM(ERROR_400);

        companion object {
            fun getCurrentState(httpStatus: Int): DeleteState? {
                values().forEach { state ->
                    if (state.httpStatus == httpStatus) {
                        return state
                    }
                }
                return null
            }
        }
    }



    /*
    *  KEEP TRACK OF CURRENT USER
    */
    private val _currentUserIdStateflow = MutableStateFlow(sharedPref.getUserId())
    val currentUserIdStateflow = _currentUserIdStateflow.asStateFlow()
    /*
    *  SHOW PROGRESS BAR ON LOADING
    */
    private val _isLoadingStateFlow = MutableStateFlow(true)
    val isLoadingStateFlow = _isLoadingStateFlow.asStateFlow()

    /*
    *  STATE FOR SWIPE REFRESH
    */
    private val _isRefreshingStateFlow = MutableStateFlow(false)
    val isRefreshingStateFlow = _isRefreshingStateFlow.asStateFlow()

    /*
    *  ID OF ARTICLE TO EXPAND
    */
    private val _expandedIdStateFlow = MutableStateFlow(0L)
    val expandedIdStateFlow = _expandedIdStateFlow.asStateFlow()
    /*
    *  SELECTED CATEGORY FOR FILTER
    */
    private val _selectedCategoryStateflow = MutableStateFlow<Int>(0)
    val selectedCategoryStateflow = _selectedCategoryStateflow.asStateFlow()

    /*
    *  LIST SHOW ON SCREEN
    */
    private val _articlesToShowStateFlow = MutableStateFlow(emptyList<ArticleDto>())
    val articlesToShowStateFlow = _articlesToShowStateFlow.asStateFlow()

    /*
     *  KEEP TRACK OF REQUEST STATE
    */
    private val _mainStateSharedFlow = MutableSharedFlow<MainState>()
    val mainStateSharedFlow = _mainStateSharedFlow.asSharedFlow()

    private val _deleteStateSharedFlow = MutableSharedFlow<DeleteState>()
    val deleteStateSharedFlow = _deleteStateSharedFlow.asSharedFlow()

    /*
    * REDIRECTION
    */
    private val _goToLoginSharedFlow = MutableSharedFlow<Screen>()
    val goToLoginSharedFlow = _goToLoginSharedFlow.asSharedFlow()

    private val _goToEditSharedFlow = MutableSharedFlow<String>()
    val goToEditSharedFlow = _goToEditSharedFlow.asSharedFlow()


    private var articlesFullList = emptyList<ArticleDto>()
    private val headers = HashMap<String, String>()
    private var isLoggedIn = true

    fun updateSelectedCategory(position: Int) {
        _selectedCategoryStateflow.value = position
        fetchArticlesListToShow()
    }

    fun handleItemClicked(item: ArticleDto) {
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

    fun refresh() {
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

        try {
            viewModelScope.launch {

                withContext(Dispatchers.IO) {
                    val responseFetchArticles = apiService.fetchAllArticles(headers)

                    if(responseFetchArticles == null) {
                        Result.Error(MainState.ERROR_SERVER)
                            .let {
                                _mainStateSharedFlow.emit(it.state)
                            }
                    } else if(responseFetchArticles.isSuccessful) {
                        Result.Success(
                           responseFetchArticles.body(),
                           responseFetchArticles.code()
                        ).let { result ->
                               articlesFullList = result.data ?: emptyList()
                                fetchArticlesListToShow()
                               _isLoadingStateFlow.value = false
                               delay(500)
                               _isRefreshingStateFlow.value = false

                            MainState.getCurrentState(result.httpStatus)?.let { state ->
                                _mainStateSharedFlow.emit(state)
                            }
                        }
                    } else {
                        Result.HttpStatus(responseFetchArticles.code())
                           .let { result->
                                MainState.getCurrentState(result.httpStatus)?.let { state ->
                                    if(isLoggedIn)
                                        _mainStateSharedFlow.emit(state)
                                }
                           }
                    }
                }
            }

        } catch (e: Exception) {
            Result.ExeptionError(
                MainState.ERROR_CONNECTION
            ).let { result->
                viewModelScope.launch {
                    _mainStateSharedFlow.emit(result.state)
                }
            }
        }
    }


    fun deleteArticle(articleId: Long) {

        headers[USER_TOKEN] = sharedPref.getToken() ?: ""

        try {
            viewModelScope.launch {

                withContext(Dispatchers.IO) {
                    val responseDeleteArticle = apiService.deleteArticle(articleId, headers)

                    if(responseDeleteArticle == null) {
                       Result.Error(DeleteState.ERROR_SERVER).let {
                           _deleteStateSharedFlow.emit(it.state)
                       }
                    } else if(responseDeleteArticle.isSuccessful) {
                        Result.Success(
                            null,
                            responseDeleteArticle.code()
                        ).let { result ->
                            fetchAllArticles()
                            DeleteState.getCurrentState(result.httpStatus)?.let { state ->
                                _deleteStateSharedFlow.emit(state)
                            }
                        }
                    } else {
                        Result.HttpStatus(responseDeleteArticle.code())
                            .let { result ->
                                DeleteState.getCurrentState(result.httpStatus)?.let { state ->
                                    _deleteStateSharedFlow.emit(state)
                                }
                            }
                    }
                }
            }

        } catch (e: Exception) {
            Result.ExeptionError(
                DeleteState.ERROR_CONNECTION
            ).let { result->
                viewModelScope.launch {
                    _deleteStateSharedFlow.emit(result.state)
                }
            }
        }
    }

    fun logout() {
        isLoggedIn = false
        sharedPref.clearSharedPref()
        viewModelScope.launch {
            _goToLoginSharedFlow.emit(Screen.Login)
        }

    }
}
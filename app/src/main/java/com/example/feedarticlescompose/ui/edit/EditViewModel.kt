package com.example.feedarticlescompose.ui.edit

import ERROR_400
import ERROR_401
import ERROR_503
import HTTP_201
import HTTP_303
import HTTP_304
import USER_TOKEN
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlescompose.dataclass.UpdateArticleDto
import com.example.feedarticlescompose.extensions.is80charactersMax
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
import javax.inject.Inject
import com.example.feedarticlescompose.utils.Result

@HiltViewModel
class EditViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sharedPref: MySharedPref
): ViewModel() {

    enum class EditState(val httpStatus: Int?) {
        ERROR_SERVER(null),
        ERROR_CONNECTION(null),
        ERROR_SERVICE(ERROR_503),
        WRONG_ID_PATH(HTTP_303),
        ERROR_AUTHORIZATION(ERROR_401),
        ERROR_PARAM(ERROR_400),
        EMPTY_FIELDS(null),
        ERROR_TITLE(null),
        FAILURE(HTTP_304),
        SUCCESS(HTTP_201);
        companion object {
            fun getCurrentState(httpStatus: Int): EditState? {
                EditState.values().forEach { state->
                    if(state.httpStatus == httpStatus) {
                        return state
                    }
                }
                return null
            }
        }
    }


    enum class FetchState (val httpStatus: Int?) {
        ERROR_SERVER(null),
        ERROR_CONNECTION(null),
        UNKNOW_USER(ERROR_401),
        UNKNOW_ARTICLE(HTTP_303),
        ERROR_PARAM(ERROR_400),
        ERROR_SERVICE(ERROR_503);

        companion object {
            fun getCurrentState(httpStatus: Int): FetchState? {
                values().forEach {state->
                    if(state.httpStatus == httpStatus) {
                        return state
                    }
                }
                return null
            }
        }
    }

   /**
    *  KEEP TRACK OF EACH FIELDS
    */
    private val _titleStateFlow = MutableStateFlow("")
    val titleStateFlow = _titleStateFlow.asStateFlow()

    private val _contentStateFlow = MutableStateFlow("")
    val contentStateFlow = _contentStateFlow.asStateFlow()

    private val _imageUrlStateFlow = MutableStateFlow("")
    val imageUrlStateFlow = _imageUrlStateFlow.asStateFlow()

    private val _selectedCategoryStateflow = MutableStateFlow(2)
    val selectedCategoryStateflow = _selectedCategoryStateflow.asStateFlow()

   /**
    *  EMIT CURRENT STATE OF REQUEST
    */
    private val _editStateSharedFlow = MutableSharedFlow<EditState>()
    val editStateSharedFlow = _editStateSharedFlow.asSharedFlow()

    private val _fetchStateSharedFlow = MutableSharedFlow<FetchState>()
    val fetchStateSharedFlow = _fetchStateSharedFlow.asSharedFlow()

   /**
    *  REDIRECTION AFTER UPDATE
    */
    private val _goToMainScreen = MutableSharedFlow<Screen>()
    val goToMainScreen = _goToMainScreen.asSharedFlow()

    private val headers = HashMap<String, String>()
    private var articleIdToUpdate: Long? = null


    fun updateArticleIdAndFetch(articleId: Long) {
        articleIdToUpdate = articleId
        fetchArticle(articleId)
    }
    fun updateTitle(title: String) {
        _titleStateFlow.value = title
    }

    fun updateContent(content: String) {
        _contentStateFlow.value = content
    }

    fun updateImageUrl(imageUrl: String) {
        _imageUrlStateFlow.value = imageUrl
    }

    fun updateSelectedCategory(position: Int) {
        _selectedCategoryStateflow.value = position
    }


    fun editArticle() {

        headers[USER_TOKEN] = sharedPref.getToken() ?: ""

        if(
            titleStateFlow.value.isNotBlank()
            &&
            contentStateFlow.value.isNotBlank()
            &&
            imageUrlStateFlow.value.isNotBlank()
        ) {
            if(titleStateFlow.value.is80charactersMax) {

                try {

                    viewModelScope.launch {

                        withContext(Dispatchers.IO) {
                            val responseEdit = apiService.updateArticle(
                                articleId = articleIdToUpdate ?: 0L,
                                headers = headers,
                                UpdateArticleDto(
                                    id = articleIdToUpdate ?: 0L,
                                    title = titleStateFlow.value,
                                    desc = contentStateFlow.value,
                                    image = imageUrlStateFlow.value,
                                    cat = selectedCategoryStateflow.value.plus(1)
                                )
                            )

                            if(responseEdit == null) {
                                Result.Error(
                                    EditState.ERROR_SERVER
                                ).let {
                                    viewModelScope.launch {
                                        _editStateSharedFlow.emit(it.state)
                                    }
                                }
                            } else if(responseEdit.isSuccessful) {
                               Result.Success(
                                   null,
                                   responseEdit.code()
                               ).let { result ->
                                   EditState.getCurrentState(result.httpStatus)?.let { state ->
                                       _editStateSharedFlow.emit(state)
                                       _goToMainScreen.emit(Screen.Main)
                                   }
                               }
                            } else {
                                Result.HttpStatus(
                                   responseEdit.code()
                                ).let { result ->
                                    EditState.getCurrentState(result.httpStatus)?.let { state ->
                                       _editStateSharedFlow.emit(state)
                                    }
                                }
                            }
                        }
                    }

                } catch (e: Exception) {
                    Result.ExeptionError(
                        EditState.ERROR_CONNECTION
                    ).let {
                        viewModelScope.launch {
                            _editStateSharedFlow.emit(it.state)
                        }
                    }
                }

            } else
                Result.Error(
                    EditState.ERROR_TITLE
                ).let {
                    viewModelScope.launch {
                        _editStateSharedFlow.emit(it.state)
                    }
                }
        } else
            Result.Error(
                EditState.EMPTY_FIELDS
            ).let {
                viewModelScope.launch {
                    _editStateSharedFlow.emit(it.state)
                }
            }
    }

    private fun fetchArticle(articleId: Long) {

        headers[USER_TOKEN] = sharedPref.getToken() ?: ""

        try {

            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                   val responseFetchArticle = apiService.fetchArticleById(headers, articleId)

                    if(responseFetchArticle?.isSuccessful == true) {
                        Result.Success(
                            responseFetchArticle.body(),
                            responseFetchArticle.code()
                        ).let { result ->
                            if(result.data != null) {
                                with(result.data) {
                                    updateTitle(titre)
                                    updateContent(descriptif)
                                    updateImageUrl(urlImage)
                                    updateSelectedCategory(categorie.minus(1))
                                }
                            }
                            FetchState.getCurrentState(result.httpStatus)?.let { state ->
                                _fetchStateSharedFlow.emit(state)
                            }
                        }
                    } else {
                        Result.HttpStatus(
                            responseFetchArticle?.code() ?: 0
                        ).let {result ->
                            FetchState.getCurrentState(result.httpStatus)?.let { state->
                                _fetchStateSharedFlow.emit(state)
                            }
                        }
                    }
               }
           }

        } catch (e: Exception) {
                Result.ExeptionError(
                    FetchState.ERROR_CONNECTION
                ).let { result ->
                    viewModelScope.launch {
                        _fetchStateSharedFlow.emit(result.state)
                    }
                }

           }
    }
}
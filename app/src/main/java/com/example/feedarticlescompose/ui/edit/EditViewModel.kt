package com.example.feedarticlescompose.ui.edit

import ERROR_400
import ERROR_401
import ERROR_503
import HTTP_201
import HTTP_303
import HTTP_304
import USER_TOKEN
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlescompose.dataclass.ArticleDto
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
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sharedPref: MySharedPref
): ViewModel() {

    enum class EditState {
        ERROR_SERVER,
        ERROR_CONNECTION,
        ERROR_SERVICE,
        WRONG_ID_PATH,
        ERROR_AUTHORIZATION,
        ERROR_PARAM,
        EMPTY_FIELDS,
        ERROR_TITLE,
        FAILURE,
        SUCCESS
    }

    enum class FetchState {
        ERROR_SERVER,
        ERROR_CONNECTION,
        UNKNOW_USER,
        UNKNOW_ARTICLE,
        ERROR_PARAM,
        ERROR_SERVICE
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

                        val responseEditArticle: Response<Unit>? = withContext(Dispatchers.IO) {
                            apiService.updateArticle(
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
                        }


                       when {
                            responseEditArticle == null ->
                                _editStateSharedFlow.emit(EditState.ERROR_SERVER)

                            responseEditArticle.isSuccessful -> {
                                _goToMainScreen.emit(Screen.Main)
                            }
                        }

                        when(responseEditArticle?.code()) {
                            HTTP_201 -> EditState.SUCCESS
                            HTTP_303 -> EditState.WRONG_ID_PATH
                            HTTP_304 -> EditState.FAILURE
                            ERROR_400 -> EditState.ERROR_PARAM
                            ERROR_401 -> EditState.ERROR_AUTHORIZATION
                            ERROR_503 -> EditState.ERROR_SERVICE
                            else -> null
                        }?.let {
                            _editStateSharedFlow.emit(it)
                        }
                    }

                } catch (e: Exception) {
                    viewModelScope.launch {
                        _editStateSharedFlow.emit(EditState.ERROR_CONNECTION)
                    }
                }

            } else
                viewModelScope.launch {
                    _editStateSharedFlow.emit(EditState.ERROR_TITLE)
                }

        } else
            viewModelScope.launch {
                _editStateSharedFlow.emit(EditState.EMPTY_FIELDS)
            }
    }

    private fun fetchArticle(articleId: Long) {

        headers[USER_TOKEN] = sharedPref.getToken() ?: ""

        try {

            viewModelScope.launch {
                val responseFetchArticle: Response<ArticleDto>? = withContext(Dispatchers.IO) {
                   apiService.fetchArticleById(headers, articleId)
                }
                val body = responseFetchArticle?.body()

                when {
                    responseFetchArticle == null ->
                        _fetchStateSharedFlow.emit(FetchState.ERROR_SERVER)

                    responseFetchArticle.isSuccessful && (body != null) -> {
                        with(body) {
                           updateTitle(titre)
                           updateContent(descriptif)
                           updateImageUrl(urlImage)
                           updateSelectedCategory(categorie.minus(1))
                        }
                    }
               }

               when(responseFetchArticle?.code()) {
                   HTTP_303 -> FetchState.UNKNOW_ARTICLE
                   ERROR_400 -> FetchState.ERROR_PARAM
                   ERROR_401 -> FetchState.UNKNOW_USER
                   ERROR_503 -> FetchState.ERROR_SERVICE
                   else -> null
               }?.let {
                   _fetchStateSharedFlow.emit(it)
               }
           }

        } catch (e: Exception) {
                viewModelScope.launch {
                    _fetchStateSharedFlow.emit(FetchState.ERROR_CONNECTION)
                }

           }
    }
}
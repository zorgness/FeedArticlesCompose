package com.example.feedarticlescompose.ui.edit

import ERROR_401
import ERROR_403
import TOKEN
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlescompose.dataclass.GetArticleDto
import com.example.feedarticlescompose.dataclass.NewArticleDto
import com.example.feedarticlescompose.dataclass.StatusDto
import com.example.feedarticlescompose.dataclass.UpdateArticleDto
import com.example.feedarticlescompose.extensions.is80charactersMax
import com.example.feedarticlescompose.network.ApiService
import com.example.feedarticlescompose.ui.creation.CreationViewModel
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
        ERROR_AUTHORIZATION,
        ERROR_PARAM,
        EMPTY_FIELDS,
        ERROR_TITLE,
        FAILURE,
        SUCCESS
    }

    private val _articleIdStateFlow = MutableStateFlow(0L)
    val articleIdStateFlow = _articleIdStateFlow.asStateFlow()

    private val _titleStateFlow = MutableStateFlow("")
    val titleStateFlow = _titleStateFlow.asStateFlow()

    private val _contentStateFlow = MutableStateFlow("")
    val contentStateFlow = _contentStateFlow.asStateFlow()

    private val _imageUrlStateFlow = MutableStateFlow("")
    val imageUrlStateFlow = _imageUrlStateFlow.asStateFlow()

    private val _selectedCategoryStateflow = MutableStateFlow<Int>(2)
    val selectedCategoryStateflow = _selectedCategoryStateflow.asStateFlow()

    private val _messageSharedFlow = MutableSharedFlow<EditState>()
    val messageSharedFlow = _messageSharedFlow.asSharedFlow()

    private val _fetchArticleSharedFlow = MutableSharedFlow<Long>()
    val fetchArticleSharedFlow = _fetchArticleSharedFlow.asSharedFlow()

    private val _goToMainScreen = MutableSharedFlow<Screen>()
    val goToMainScreen = _goToMainScreen.asSharedFlow()

    private var message: EditState? = null
    private val headers = HashMap<String, String>()

    init {
        viewModelScope.launch {
            _fetchArticleSharedFlow.emit(articleIdStateFlow.value)
        }
    }

    fun updateArticleId(articleId: Long) {
       _articleIdStateFlow.value = articleId

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

        headers["token"] = sharedPref.getToken() ?: ""

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

                        val responseEditArticle: Response<StatusDto>? = withContext(Dispatchers.IO) {
                            apiService.updateArticle(
                                articleId = articleIdStateFlow.value,
                                headers = headers,
                                UpdateArticleDto(
                                    id = sharedPref.getUserId(),
                                    title = titleStateFlow.value,
                                    desc = contentStateFlow.value,
                                    image = imageUrlStateFlow.value,
                                    cat = selectedCategoryStateflow.value.plus(1)
                                )
                            )
                        }

                        val body = responseEditArticle?.body()

                        when {
                            responseEditArticle?.body() == null ->
                                message = EditState.ERROR_SERVER

                            responseEditArticle.isSuccessful && (body != null) -> {
                                message = EditState.SUCCESS
                                _goToMainScreen.emit(Screen.Main)
                            }

                            responseEditArticle.code() == ERROR_401 ->
                                message = EditState.ERROR_PARAM

                            responseEditArticle.code() == ERROR_403 ->
                                message = EditState.ERROR_AUTHORIZATION
                        }

                    }

                } catch (e: Exception) {
                    message = EditState.ERROR_CONNECTION
                }

            } else {
                message = EditState.ERROR_TITLE
            }

        } else {
            message = EditState.EMPTY_FIELDS
        }

    }

    fun fetchArticle(articleId: Long) {

        Log.d("fetchedArticle", "articleId: $articleId")
        _articleIdStateFlow.value = articleId

        headers[TOKEN] = sharedPref.getToken() ?: ""

       try {

           viewModelScope.launch {
               val responseFetchArticle: Response<GetArticleDto>? = withContext(Dispatchers.IO) {
                   apiService.fetchArticleById(headers, articleId)
               }
               val body = responseFetchArticle?.body()
               when {
                   responseFetchArticle?.body() == null ->
                       message = EditState.ERROR_SERVER

                   responseFetchArticle.isSuccessful && (body != null) -> {
                       with(body.article) {
                           updateTitle(titre)
                           updateContent(descriptif)
                           updateImageUrl(urlImage)
                           updateSelectedCategory(categorie.minus(1))
                       }
                   }

                   responseFetchArticle.code() == ERROR_403 ->
                       message = EditState.ERROR_AUTHORIZATION

               }

           }

       } catch (e: Exception) {
            message = EditState.ERROR_CONNECTION
       }
    }
}
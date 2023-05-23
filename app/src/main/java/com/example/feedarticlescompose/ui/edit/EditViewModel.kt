package com.example.feedarticlescompose.ui.edit

import ERROR_403
import TOKEN
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlescompose.dataclass.GetArticleDto
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

    private val _goToMainScreen = MutableSharedFlow<Screen>()
    val goToMainScreen = _goToMainScreen.asSharedFlow()

    private var message: EditState? = null
    private val headers = HashMap<String, String>()


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

    fun fetchArticle(articleId: Long) {

        _articleIdStateFlow.value = articleId

        headers[TOKEN] = sharedPref.getToken() ?: ""

       try {

           viewModelScope.launch {
               val responseFetch: Response<GetArticleDto>? = withContext(Dispatchers.IO) {
                   apiService.fetchArticleById(headers, articleId)
               }
               val body = responseFetch?.body()
               when {
                   responseFetch?.body() == null ->
                       message = EditState.ERROR_SERVER

                   responseFetch.isSuccessful && (body != null) -> {
                       with(body.article) {
                           updateTitle(titre)
                           updateContent(descriptif)
                           updateImageUrl(urlImage)
                           updateSelectedCategory(categorie.minus(1))
                       }
                   }

                   responseFetch.code() == ERROR_403 ->
                       message = EditState.ERROR_AUTHORIZATION

               }

           }

       } catch (e: Exception) {
            message = EditState.ERROR_CONNECTION
       }
    }
}
package com.example.feedarticlescompose.ui.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlescompose.dataclass.NewArticleDto
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
class CreationViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sharedPref: MySharedPref
): ViewModel() {

    enum class CreationState {
        ERROR_SERVER,
        ERROR_CONNECTION,
        ERROR_AUTHORIZATION,
        ERROR_PARAM,
        EMPTY_FIELDS,
        ERROR_TITLE,
        FAILURE,
        SUCCESS
    }

    private val _titleStateFlow = MutableStateFlow("")
    val titleStateFlow = _titleStateFlow.asStateFlow()

    private val _contentStateFlow = MutableStateFlow("")
    val contentStateFlow = _contentStateFlow.asStateFlow()

    private val _imageUrlStateFlow = MutableStateFlow("")
    val imageUrlStateFlow = _imageUrlStateFlow.asStateFlow()

    private val _selectedCategoryStateflow = MutableStateFlow<Int>(2)
    val selectedCategoryStateflow = _selectedCategoryStateflow.asStateFlow()

    private val _messageSharedFlow = MutableSharedFlow<CreationState>()
    val messageSharedFlow = _messageSharedFlow.asSharedFlow()

    private val _goToMainScreen = MutableSharedFlow<Screen>()
    val goToMainScreen = _goToMainScreen.asSharedFlow()


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

    private var message: CreationState? = null
    private val headers = HashMap<String, String>()

    fun newArticle() {

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

                        val responseNewArticle: Response<Unit>? = withContext(Dispatchers.IO) {
                            apiService.addNewArticle(
                                NewArticleDto(
                                    title = titleStateFlow.value,
                                    desc = contentStateFlow.value,
                                    image = imageUrlStateFlow.value,
                                    idU = sharedPref.getUserId(),
                                    cat = selectedCategoryStateflow.value.plus(1)
                                ),
                                headers = headers
                            )
                        }

                        val body = responseNewArticle?.body()

                        when {
                            responseNewArticle == null ->
                                message = CreationState.ERROR_SERVER

                            responseNewArticle.isSuccessful && (body != null) -> {
                                message = CreationState.SUCCESS
                                _goToMainScreen.emit(Screen.Main)
                            }


                        }

                    }

                } catch (e: Exception) {
                    message = CreationState.ERROR_CONNECTION
                }

            } else {
                message = CreationState.ERROR_TITLE
            }

        } else {
            message = CreationState.EMPTY_FIELDS
        }

        message?.let {
            viewModelScope.launch {
                _messageSharedFlow.emit(it)
            }
        }
    }
}
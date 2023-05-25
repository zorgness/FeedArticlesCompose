package com.example.feedarticlescompose.ui.creation

import ERROR_400
import ERROR_401
import ERROR_503
import HTTP_201
import HTTP_304
import USER_TOKEN
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlescompose.dataclass.NewArticleDto
import com.example.feedarticlescompose.extensions.is80charactersMax
import com.example.feedarticlescompose.network.ApiService
import com.example.feedarticlescompose.ui.register.RegisterViewModel
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
import com.example.feedarticlescompose.utils.Result


@HiltViewModel
class CreationViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sharedPref: MySharedPref
): ViewModel() {

    enum class CreationState(val httpStatus: Int?) {
        ERROR_SERVER(null),
        ERROR_CONNECTION(null),
        ERROR_AUTHORIZATION(ERROR_401),
        ERROR_PARAM(ERROR_400),
        EMPTY_FIELDS(null),
        ERROR_TITLE(null),
        FAILURE(HTTP_304),
        SUCCESS(HTTP_201),
        ERROR_SERVICE(ERROR_503);

        companion object {
            fun getState(httpStatus: Int): CreationState? {
                CreationState.values().forEach { state ->
                    if (state.httpStatus == httpStatus) {
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

    private val _selectedCategoryStateflow = MutableStateFlow<Int>(2)
    val selectedCategoryStateflow = _selectedCategoryStateflow.asStateFlow()

   /**
    *  EMIT CURRENT STATE OF REQUEST
    */
    private val _creationStateSharedFlow = MutableSharedFlow<CreationState>()
    val creationStateSharedFlow = _creationStateSharedFlow.asSharedFlow()

   /**
    *  REDIRECTION TO MAIN AFTER INSERT
    */
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

    private val headers = HashMap<String, String>()

    fun newArticle() {

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
                            val responseNewArticle = apiService.addNewArticle(
                                NewArticleDto(
                                    title = titleStateFlow.value,
                                    desc = contentStateFlow.value,
                                    image = imageUrlStateFlow.value,
                                    idU = sharedPref.getUserId(),
                                    cat = selectedCategoryStateflow.value.plus(1)
                                ),
                                headers = headers
                            )

                            if(responseNewArticle?.isSuccessful == true) {
                                Result.Success(
                                    null,
                                    responseNewArticle.code()
                                ).let { result ->
                                    CreationState.getState(result.httpStatus)
                                        ?.let { state ->
                                            _creationStateSharedFlow.emit(state)
                                            _goToMainScreen.emit(Screen.Main)
                                        }
                                }
                            } else {
                                Result.HttpStatus(
                                    responseNewArticle?.code() ?: 0
                                ).let { result ->
                                    CreationState.getState(result.httpStatus)
                                        ?.let { state ->
                                                _creationStateSharedFlow.emit(state)
                                        }
                                }
                            }
                        }

                    }

                } catch (e: Exception) {
                    Result.ExeptionError(
                        CreationState.ERROR_CONNECTION
                    ).let {
                        viewModelScope.launch {
                            _creationStateSharedFlow.emit(it.state)
                        }
                    }
                }

            } else {
                Result.Failure(
                    CreationState.ERROR_TITLE
                ).let {
                    viewModelScope.launch {
                        _creationStateSharedFlow.emit(it.state)
                    }
                }
            }

        } else {
            Result.Failure(
                CreationState.EMPTY_FIELDS
            ).let {
                viewModelScope.launch {
                    _creationStateSharedFlow.emit(it.state)
                }
            }
        }
    }
}
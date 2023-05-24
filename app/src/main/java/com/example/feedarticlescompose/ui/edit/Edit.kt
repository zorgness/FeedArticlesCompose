package com.example.feedarticlescompose.ui.edit

import CustomTextField
import RadioButtonsNewEditGroup
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.feedarticlescompose.R


@Composable
fun EditScreen(
    navController: NavHostController,
    viewModel: EditViewModel,
) {
    val title by viewModel.titleStateFlow.collectAsState()
    val content by viewModel.contentStateFlow.collectAsState()
    val imageUrl by viewModel.imageUrlStateFlow.collectAsState()
    val selectedCategory by viewModel.selectedCategoryStateflow.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.goToMainScreen.collect {
            navController.navigate(it.route) {
                popUpTo(it.route) {
                    inclusive = true
                }
            }
        }
    }


    LaunchedEffect(true ) {
         viewModel.editStateSharedFlow.collect { state ->
             when(state) {
                 EditViewModel.EditState.SUCCESS -> R.string.update_success
                 EditViewModel.EditState.FAILURE -> R.string.update_failure
                 EditViewModel.EditState.ERROR_PARAM -> R.string.error_param
                 EditViewModel.EditState.ERROR_SERVER -> R.string.error_server
                 EditViewModel.EditState.ERROR_CONNECTION -> R.string.error_connection
                 EditViewModel.EditState.EMPTY_FIELDS -> R.string.empty_fields
                 EditViewModel.EditState.ERROR_TITLE -> R.string.error_title
                 EditViewModel.EditState.ERROR_SERVICE -> R.string.error_service
                 EditViewModel.EditState.WRONG_ID_PATH -> R.string.wrong_id_path
                 EditViewModel.EditState.ERROR_AUTHORIZATION -> R.string.error_authorization
             }.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(true ) {
        viewModel.fetchStateSharedFlow.collect { state ->
            when(state) {
                EditViewModel.FetchState.ERROR_CONNECTION -> R.string.error_connection
                EditViewModel.FetchState.ERROR_SERVER -> R.string.error_server
                EditViewModel.FetchState.UNKNOW_USER -> R.string.unknow_user
                EditViewModel.FetchState.UNKNOW_ARTICLE -> R.string.unknow_article
                EditViewModel.FetchState.ERROR_PARAM -> R.string.error_param
                EditViewModel.FetchState.ERROR_SERVICE -> R.string.error_service
            }.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    EditContent(
        context = context,
        title,
        content,
        imageUrl,
        selectedCategory,
        handleTitle = { viewModel.updateTitle(it) },
        handleContent = { viewModel.updateContent(it) },
        handleImageUrl = { viewModel.updateImageUrl(it) },
        handleCategoryPosition = { viewModel.updateSelectedCategory(it)},
        handleClick = { viewModel.editArticle() }
    )
}


@Composable
fun EditContent(
    context: Context,
    title: String,
    content: String,
    imageUrl: String,
    selectedCategory: Int,
    handleTitle: (String) -> Unit,
    handleContent: (String) -> Unit,
    handleImageUrl: (String) -> Unit,
    handleCategoryPosition: (Int) -> Unit,
    handleClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = context.getString(R.string.edit_article),
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )

        CustomTextField(
            placeholder = context.getString(R.string.title),
            value = title,
            handleValue = { handleTitle(it) }
        )

        CustomTextField(
            placeholder = context.getString(R.string.content),
            value = content,
            handleValue = { handleContent(it) },
            customHeight = 120
        )
        CustomTextField(
            placeholder = context.getString(R.string.image_url),
            value = imageUrl,
            handleValue = { handleImageUrl(it) }
        )

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.feedarticles_logo),
            modifier = Modifier
                .size(80.dp)

        )

        RadioButtonsNewEditGroup(
            selectedCategory,
            onRadioSelected = { handleCategoryPosition(it) }
        )

        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { handleClick() }
        ) {
            Text(
                text = context.getString(R.string.update),
                color = Color.White
            )
        }
    }

}




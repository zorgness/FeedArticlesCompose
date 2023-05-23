import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.FabPosition
import androidx.compose.material.RadioButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.feedarticlescompose.R
import com.example.feedarticlescompose.ui.form.FormViewModel
import com.example.feedarticlescompose.ui.theme.FeedArticlesComposeTheme
import kotlinx.coroutines.flow.collect
import java.util.Locale.Category


@Composable
fun FormScreen(
    navController: NavHostController,
    viewModel: FormViewModel
) {
    val title by viewModel.titleStateFlow.collectAsState()
    val content by viewModel.contentStateFlow.collectAsState()
    val imageUrl by viewModel.imageUrlStateFlow.collectAsState()
    val selectedCategory by viewModel.selectedCategoryStateflow.collectAsState()

    LaunchedEffect(true) {
        viewModel.goToMainScreen.collect {
            navController.navigate(it.route)
        }
    }


    LaunchedEffect(true ) {
        viewModel.messageSharedFlow.collect { message ->
            when(message) {
                FormViewModel.FormState.SUCCESS -> R.string.new_success
                FormViewModel.FormState.ERROR_PARAM -> R.string.error_param
                FormViewModel.FormState.ERROR_SERVER -> R.string.error_server
                FormViewModel.FormState.ERROR_CONNECTION -> R.string.error_connection
                FormViewModel.FormState.ERROR_AUTHORIZATION -> R.string.error_authorization
                FormViewModel.FormState.EMPTY_FIELDS -> R.string.empty_fields
                FormViewModel.FormState.ERROR_TITLE -> R.string.error_title
                FormViewModel.FormState.FAILURE -> R.string.new_failure
            }
        }
    }

    FormContent(
        title,
        content,
        imageUrl,
        selectedCategory,
        handleTitle = { viewModel.updateTitle(it) },
        handleContent = { viewModel.updateContent(it) },
        handleImageUrl = { viewModel.updateImageUrl(it) },
        handleCategoryPosition = { viewModel.updateSelectedCategory(it)},
        handleClick = {viewModel.newArticle()}

    )
}

@Composable
fun FormContent(
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
            text = "Nouvel Article",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
        )

        CustomTextField(
            placeholder = "Titre" ,
            value = title,
            handleValue = { handleTitle(it) }
        )

        CustomTextField(
            placeholder = "Contenu" ,
            value = content,
            handleValue = { handleContent(it) },
            customHeight = 80
        )
        CustomTextField(
            placeholder = "Image URL" ,
            value = imageUrl,
            handleValue = { handleImageUrl(it) }
        )

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.feedarticles_logo),
            /*onSuccess = { isVisible = true },
            onError = { isVisible = false },*/
            modifier = Modifier
                .size(80.dp)

        )

        RadioButtonsGroup(
            selectedCategory,
            onRadioSelected = { handleCategoryPosition(it) }
        )

        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { handleClick() }
        ) {
            Text(
                text = "Enregister",
                color = Color.White
            )
        }
    }

}

@Composable
fun RadioButtonsGroup(
    selectedCategory: Int,
    onRadioSelected: (Int) -> Unit
) {

    val categories = listOf("Sport", "Manga", "Divers")
    Row(
        Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        categories.forEachIndexed { index, category ->
                RadioButton(
                    selected = selectedCategory == index,
                    onClick = { onRadioSelected(index) }
                )
                Text(
                    text = category,
                    color = Color.Black
                )
        }

    }
}


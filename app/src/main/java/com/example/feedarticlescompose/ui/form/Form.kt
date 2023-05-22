import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.example.feedarticlescompose.ui.theme.FeedArticlesComposeTheme
import java.util.Locale.Category

@Composable
fun FormScreen(
    navController: NavHostController,
) {
    FormContent()
}

@Composable
fun FormContent() {

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
            value = "",
            handleValue = {

            }
        )
        CustomTextField(
            placeholder = "Contenu" ,
            value = "",
            handleValue = {

            }
        )
        CustomTextField(
            placeholder = "Image URL" ,
            value = "",
            handleValue = {

            }
        )

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("")
                .crossfade(true)
                .build(),
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.feedarticles_logo),
            /*onSuccess = { isVisible = true },
            onError = { isVisible = false },*/
            modifier = Modifier
                .size(80.dp)

        )

        RadioButtonsGroup()

        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { /*TODO*/ }
        ) {
            Text(
                text = "Enregister",
                color = Color.White
            )
        }
    }

}

@Composable
fun RadioButtonsGroup() {

    val categories = listOf("Sport", "Manga", "Divers")
    Row(Modifier.padding(8.dp)) {

        categories.forEach { category ->
                RadioButton(
                    selected = false,
                    onClick = null
                )
                Text(
                    text = category,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 8.dp)

                )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FeedArticlesComposeTheme {
        FormContent()
    }
}
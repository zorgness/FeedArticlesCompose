import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.feedarticlescompose.ui.theme.FeedArticlesComposeTheme
import com.example.feedarticlescompose.utils.Screen

@Composable
fun RegisterScreen(
    navController: NavHostController,
) {
    RegisterContent()
}

@Composable
fun RegisterContent() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
    ) {
        Text(
            text = "Nouveau Compte",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)


        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 40.dp)
                .padding(bottom = 140.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextField(
                placeholder = "Login" ,
                value = "",
                handleValue = {

                }
            )
            Spacer(modifier = Modifier.height(40.dp))
            CustomTextField(
                placeholder = "Password" ,
                value = "",
                handleValue = {

                }
            )
            Spacer(modifier = Modifier.height(40.dp))
            CustomTextField(
                placeholder = "Confirmation Password" ,
                value = "",
                handleValue = {

                }
            )
        }


        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                onClick = { /*TODO*/ }
            ) {
                Text(
                    text = "S'inscrire",
                    color = Color.White
                )
            }

        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FeedArticlesComposeTheme {
        RegisterContent()
    }
}
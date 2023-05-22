import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.example.feedarticlescompose.ui.theme.FeedArticlesComposeTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feedarticlescompose.R
import com.example.feedarticlescompose.ui.theme.BlueApp


@Composable
fun SplashScreen(
    navController: NavHostController,
) {
    SplashContent()
}

@Composable
fun SplashContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueApp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly

    ) {

        Image(
            painter = painterResource(id = R.drawable.feedarticles_logo),
            contentDescription = null,
            modifier = Modifier.size(300.dp)
        )

        Text(
            text= "Feed Articles",
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FeedArticlesComposeTheme {
        SplashContent()
    }
}
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.feedarticlescompose.ui.login.LoginViewModel
import com.example.feedarticlescompose.ui.theme.FeedArticlesComposeTheme
import com.example.feedarticlescompose.utils.Screen
import kotlinx.coroutines.flow.collect
import com.example.feedarticlescompose.R


@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel
) {
    val login by viewModel.loginStateFlow.collectAsState("")
    val password by viewModel.passwordStateFlow.collectAsState("")

    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.goToMainSharedFlow.collect {
            navController.navigate(it.route) {
                popUpTo(it.route) {
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(true ) {
        viewModel.messageSharedFlow.collect {message->
            when(message) {
                LoginViewModel.LoginState.ERROR_SERVER -> R.string.error_server
                LoginViewModel.LoginState.ERROR_AUTHORIZATION -> R.string.error_authorization
                LoginViewModel.LoginState.ERROR_CONNECTION -> R.string.error_connection
                LoginViewModel.LoginState.WRONG_CREDENTIAL -> R.string.wrong_credential
                LoginViewModel.LoginState.EMPTY_FIELDS -> R.string.empty_fields
            }.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LoginContent(
        login = login,
        password = password,
        handleLogin = { viewModel.updateLogin(it)},
        handlePassword = { viewModel.updatePassword(it)},
        handleClick = { viewModel.login() },
        goToRegister = {
            navController.navigate(Screen.Register.route)
        }
    )
}

@Composable
fun LoginContent(
    login: String,
    password: String,
    handleLogin: (String) -> Unit,
    handlePassword: (String) -> Unit,
    handleClick: () -> Unit,
    goToRegister: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
    ) {
        Text(
            text = "Connectez-vous",
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
                value = login,
                handleValue = { handleLogin(it) }
            )
            Spacer(modifier = Modifier.height(40.dp))
            CustomTextField(
                placeholder = "Password" ,
                value = password,
                handleValue = { handlePassword(it) }
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
                onClick = { handleClick() }
            ) {
                Text(
                    text = "Se connecter",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Pas de compte ? inscrivez-vous !",
                modifier = Modifier.clickable { goToRegister() }

            )
        }
    }



}


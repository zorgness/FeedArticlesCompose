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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.feedarticlescompose.ui.register.RegisterViewModel
import com.example.feedarticlescompose.ui.theme.FeedArticlesComposeTheme
import com.example.feedarticlescompose.utils.Screen
import com.example.feedarticlescompose.R

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: RegisterViewModel
) {
    val login by viewModel.loginStateFlow.collectAsState("")
    val password by viewModel.passwordStateFlow.collectAsState("")
    val confirm by viewModel.confirmStateFlow.collectAsState("")

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

    LaunchedEffect(true) {
        viewModel.messageSharedFlow.collect {message->
            when(message) {
                RegisterViewModel.RegisterState.FAILURE -> R.string.account_failed
                RegisterViewModel.RegisterState.ERROR_SERVER -> R.string.error_server
                RegisterViewModel.RegisterState.ERROR_AUTHORIZATION -> R.string.error_authorization
                RegisterViewModel.RegisterState.ERROR_CONNECTION -> R.string.error_connection
                RegisterViewModel.RegisterState.ERROR_CONFIRMATION -> R.string.error_confirmation
                RegisterViewModel.RegisterState.EMPTY_FIELDS -> R.string.empty_fields
            }.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    RegisterContent(
        login = login,
        password = password,
        confirm = confirm,
        handleLogin = { viewModel.updateLogin(it) },
        handlePassword = { viewModel.updatePassword(it) },
        handleConfirm = { viewModel.updateConfirm(it) },
        handleClick = { viewModel.register() }
    )
}

@Composable
fun RegisterContent(
    login: String,
    password: String,
    confirm: String,
    handleLogin: (String) -> Unit,
    handlePassword: (String) -> Unit,
    handleConfirm: (String) -> Unit,
    handleClick: () -> Unit
) {

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
                value = login,
                handleValue = { handleLogin(it) }
            )
            Spacer(modifier = Modifier.height(40.dp))
            CustomTextField(
                placeholder = "Password" ,
                value = password,
                handleValue = { handlePassword(it) }
            )
            Spacer(modifier = Modifier.height(40.dp))
            CustomTextField(
                placeholder = "Confirmation Password" ,
                value = confirm,
                handleValue = { handleConfirm(it) }
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
                    text = "S'inscrire",
                    color = Color.White
                )
            }

        }
    }

}

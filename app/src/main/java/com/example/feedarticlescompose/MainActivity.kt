package com.example.feedarticlescompose

import CreationScreen
import com.example.feedarticlescompose.ui.edit.EditScreen
import LoginScreen
import com.example.feedarticlescompose.ui.main.MainScreen
import RegisterScreen
import SplashScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.feedarticlescompose.ui.creation.CreationViewModel
import com.example.feedarticlescompose.ui.edit.EditViewModel
import com.example.feedarticlescompose.ui.login.LoginViewModel
import com.example.feedarticlescompose.ui.main.MainViewModel
import com.example.feedarticlescompose.ui.register.RegisterViewModel
import com.example.feedarticlescompose.ui.splash.SplashViewModel
import com.example.feedarticlescompose.ui.theme.FeedArticlesComposeTheme
import com.example.feedarticlescompose.utils.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedArticlesComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@OptIn(ExperimentalUnitApi::class, ExperimentalMaterialApi::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Splash.route ) {
        composable(Screen.Splash.route) {
            val splashViewModel: SplashViewModel = hiltViewModel()
            SplashScreen(navController, splashViewModel)
        }
        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(navController, loginViewModel)
        }
        composable(Screen.Register.route) {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(navController, registerViewModel)
        }
        composable(Screen.Main.route) {
            val mainViewModel: MainViewModel = hiltViewModel()
            mainViewModel.fetchAllArticles()
            MainScreen(navController, mainViewModel)
        }
        composable(Screen.Creation.route) {
            val creationViewModel: CreationViewModel = hiltViewModel()
            CreationScreen(navController, creationViewModel)
        }
        composable(
            Screen.Edit.route + "/{articleId}",
            arguments = listOf(
                navArgument("articleId") {
                    type = NavType.LongType
                }
            )
        ) {
            val editViewModel: EditViewModel = hiltViewModel()
            val articleId = it.arguments?.getLong("articleId") ?: 0L
            editViewModel.updateArticleIdAndFetch(articleId)
            EditScreen(navController, editViewModel)
        }
    }
}

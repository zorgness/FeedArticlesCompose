package com.example.feedarticlescompose

import FormScreen
import LoginScreen
import MainScreen
import RegisterScreen
import SplashScreen
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.feedarticlescompose.ui.main.MainViewModel
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

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Splash.route ) {
        composable(Screen.Splash.route) {
            val splashViewModel: SplashViewModel = hiltViewModel()
            SplashScreen(navController, splashViewModel)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.Main.route) {
            val mainViewModel: MainViewModel = hiltViewModel()
            MainScreen(navController, mainViewModel)
        }
        composable(Screen.Form.route) {
            FormScreen(navController)
        }
    }



}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FeedArticlesComposeTheme {

    }
}
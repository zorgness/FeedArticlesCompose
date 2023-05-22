package com.example.feedarticlescompose.utils

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Form : Screen("form")
    object Main : Screen("main")
    object Login : Screen("login")
    object Register : Screen("register")
}

package com.example.feedarticlescompose.utils

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Creation : Screen("creation")
    object Edit : Screen("edit")
    object Main : Screen("main")
    object Login : Screen("login")
    object Register : Screen("register")
}

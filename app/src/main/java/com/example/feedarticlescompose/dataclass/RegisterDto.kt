package com.example.feedarticlescompose.dataclass

import com.squareup.moshi.Json

data class RegisterDto(
    @Json(name = "login")
    val login: String,
    @Json(name = "mdp")
    val mdp: String
)
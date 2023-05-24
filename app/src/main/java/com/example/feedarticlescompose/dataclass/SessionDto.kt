package com.example.feedarticlescompose.dataclass

import com.squareup.moshi.Json

data class SessionDto(
    @Json(name = "id")
    val id: Long,
    @Json(name = "token")
    val token: String?
)

package com.example.feedarticlescompose.dataclass

import com.squareup.moshi.Json

data class UpdateArticleDto(
    @Json(name = "id")
    val id: Long,
    @Json(name = "title")
    val title: String,
    @Json(name = "desc")
    val desc: String,
    @Json(name = "image")
    val image: String,
    @Json(name = "cat")
    val cat: Int
)
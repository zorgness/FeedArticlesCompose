package com.example.feedarticlescompose.dataclass

import com.squareup.moshi.Json

data class GetArticlesDto(
    @Json(name = "")
    val articles: List<ArticleDto> = listOf()
)

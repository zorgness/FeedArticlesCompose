package com.example.feedarticlescompose.dataclass

import com.squareup.moshi.Json

data class GetArticleDto(
    @Json(name = "")
    val article: ArticleDto
)
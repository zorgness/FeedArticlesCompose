package com.example.feedarticlescompose.dataclass

import com.squareup.moshi.Json

sealed class ArticleData {

    data class CategoryData(
        val id: Int,
        val name: String,
        val color: Int
    ) : ArticleData()

    data class ArticleDto(
        @Json(name = "id")
        val id: Long,
        @Json(name = "titre")
        val titre: String,
        @Json(name = "descriptif")
        val descriptif: String,
        @Json(name = "url_image")
        val urlImage: String,
        @Json(name = "categorie")
        val categorie: Int,
        @Json(name = "created_at")
        val createdAt: String,
        @Json(name = "id_u")
        val idU: Long,
    ) : ArticleData()

}

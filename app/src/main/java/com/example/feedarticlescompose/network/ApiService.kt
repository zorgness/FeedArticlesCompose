package com.example.feedarticlescompose.network

import com.example.feedarticlescompose.dataclass.ArticleDto
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET(ApiRoutes.ARTICLES)
    suspend fun fetchArticles() : Response<List<ArticleDto>>?
}
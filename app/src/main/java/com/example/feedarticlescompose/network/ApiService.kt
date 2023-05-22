package com.example.feedarticlescompose.network

import com.example.feedarticlescompose.dataclass.ArticleDto
import com.example.feedarticlescompose.dataclass.SessionDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST(ApiRoutes.USER)
    suspend fun login(
        @Field("login") login: String,
        @Field("mdp") mdp: String
    ): Response<SessionDto>?

    @GET(ApiRoutes.ARTICLES)
    suspend fun fetchArticles() : Response<List<ArticleDto>>?
}
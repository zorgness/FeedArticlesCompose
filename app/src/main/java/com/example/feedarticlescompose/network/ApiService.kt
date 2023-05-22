package com.example.feedarticlescompose.network

import com.example.feedarticlescompose.dataclass.ArticleDto
import com.example.feedarticlescompose.dataclass.RegisterDto
import com.example.feedarticlescompose.dataclass.SessionDto
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @PUT(ApiRoutes.USER)
    suspend fun register(@Body registerDto: RegisterDto): Response<SessionDto>?
    @FormUrlEncoded
    @POST(ApiRoutes.USER)
    suspend fun login(
        @Field("login") login: String,
        @Field("mdp") mdp: String
    ): Response<SessionDto>?

    @GET(ApiRoutes.ARTICLES)
    suspend fun fetchArticles() : Response<List<ArticleDto>>?
}
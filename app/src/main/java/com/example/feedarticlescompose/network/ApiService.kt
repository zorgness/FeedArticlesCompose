package com.example.feedarticlescompose.network

import com.example.feedarticlescompose.dataclass.*
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
    suspend fun fetchAllArticles (
        @HeaderMap headers: Map<String, String>,
    ): Response<GetArticlesDto>?

    @PUT(ApiRoutes.ARTICLES)
    suspend fun addNewArticle(
        @Body newArticleDto: NewArticleDto,
        @HeaderMap headers: Map<String, String>,
    ): Response<StatusDto>?

 @GET(ApiRoutes.ARTICLES)
    suspend fun fetchArticleById(
        @HeaderMap headers: Map<String, String>,
        @Query("id") articleId: Long,
    ): Response<GetArticleDto>?

 @POST(ApiRoutes.ARTICLES)
    suspend fun updateArticle(
        @Query("id") articleId: Long,
        @HeaderMap headers: Map<String, String>,
        @Body updateArticleDto: UpdateArticleDto

    ): Response<StatusDto>?

    @DELETE(ApiRoutes.ARTICLES)
    suspend fun deleteArticle(
        @Query("id") articleId: Long,
        @HeaderMap headers: Map<String, String>
    ): Response<StatusDto>?
}
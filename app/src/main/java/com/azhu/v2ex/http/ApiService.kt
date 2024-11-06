package com.azhu.v2ex.http

import okhttp3.ResponseBody
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * @description:
 * @author: azhu
 * @date: 2024-10-02 17:55
 * @version: 1.0.0
 */
interface ApiService {

    @GET("/")
    suspend fun getTopicList(@Query("tab") tab: String? = null): ResponseBody

    @GET("/recent")
    suspend fun getRecentTopicList(@Query("p") page: Int = 1): ResponseBody

    @GET("/t/{sid}")
    suspend fun getTopicDetails(@Path("sid") sid: String, @Query("p") page: Int = 1): ResponseBody

    @GET("/favorite/topic/{tid}")
    suspend fun favorite(@Path("tid") tid: String, @Query("once") once: String, @Header("Referer") referer: String): ResponseBody

    @GET("/unfavorite/topic/{tid}")
    suspend fun unfavorite(
        @Path("tid") tid: String,
        @Query("once") once: String,
        @Header("Referer") referer: String
    ): ResponseBody

    @GET("/ignore/topic/{tid}")
    suspend fun ignore(@Path("tid") tid: String, @Query("once") once: String, @Header("Referer") referer: String): ResponseBody

    @POST("/thank/topic/{tid}")
    suspend fun thank(@Path("tid") tid: String, @Query("once") once: String): ResponseBody

    @POST("/thank/reply/{rid}")
    suspend fun thankReply(@Path("rid") rid: String, @Query("once") once: String): ResponseBody

    @FormUrlEncoded
    @POST("/t/{tid}")
    suspend fun replyTopic(@Path("tid") tid: String, @FieldMap form: Map<String, String>): ResponseBody

    @GET("/member/{username}")
    suspend fun getMemberDetails(@Path("username") username: String): ResponseBody

    @GET("/mission/daily")
    suspend fun getUserProfile(): ResponseBody

    @GET("/signin")
    suspend fun getSigninParams(): ResponseBody

    @FormUrlEncoded
    @POST("/signin")
    suspend fun signin(
        @FieldMap form: Map<String, String>,
        @Header("Origin") origin: String = "https://www.v2ex.com",
        @Header("Referer") referer: String = "https://www.v2ex.com/signin?next=/mission/daily"
    ): ResponseBody

    @GET("/mission/daily/redeem")
    suspend fun claimLoginRewards(
        @Query("once") once: String,
        @Header("Referer") referer: String = "https://www.v2ex.com/signin?next=/mission/daily",
        @Header("UA") ua: String = "mobile",
    ): ResponseBody

    @GET("/signout")
    suspend fun logout(
        @Query("once") nonce: String,
        @Header("Referer") referer: String
    ): ResponseBody

    @GET("/my/nodes")
    suspend fun getNodesFromCollection(): ResponseBody

    @GET("/my/topics")
    suspend fun getTopicsFromCollection(): ResponseBody

    @GET("/my/following")
    suspend fun getFollowing(@Query("p") page: Int): ResponseBody

    @GET("/go/{node}")
    suspend fun getTopicsByNode(@Path("node") node: String, @Query("p") page: Int): ResponseBody

    @GET("/member/{username}/topics")
    suspend fun getAllTopicsByUser(@Path("username") node: String, @Query("p") page: Int): ResponseBody

    @GET("/member/{username}/replies")
    suspend fun getAllRepliesByUser(@Path("username") node: String, @Query("p") page: Int): ResponseBody

    @GET("/")
    suspend fun getNodeNavigation(): ResponseBody

    @GET
    suspend fun action(@Url action: String): ResponseBody
}
package com.azhu.v2ex.http

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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
    suspend fun getTopicDetails(@Path("sid") sid: String): ResponseBody

    @GET("/member/{username}")
    suspend fun getMemberDetails(@Path("username") username: String): ResponseBody

}
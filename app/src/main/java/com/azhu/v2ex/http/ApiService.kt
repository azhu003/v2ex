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
    suspend fun getSubjectList(@Query("tab") tab: String? = null): ResponseBody

    @GET("/t/{sid}")
    suspend fun getSubjectDetails(@Path("sid") sid: String): ResponseBody

    @GET("/member/{username}")
    suspend fun getMemberDetails(@Path("username") username: String): ResponseBody

}
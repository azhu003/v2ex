package com.azhu.v2ex.http

import okhttp3.Response
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

    @GET("/t/{id}")
    suspend fun getSubjectDetails(@Path("id") id: Int): ResponseBody

}
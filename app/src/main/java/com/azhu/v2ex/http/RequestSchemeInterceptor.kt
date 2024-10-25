package com.azhu.v2ex.http

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Http -> Https
 * @author: Jerry
 * @date: 2024-10-25 19:25
 * @version: 1.0.0
 */
class RequestSchemeInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url
        val scheme = url.scheme
        request = if ("http" == scheme) {
            request.newBuilder().url(url.newBuilder().scheme("https").build()).build()
        } else {
            request.newBuilder().build()
        }
        return chain.proceed(request)
    }
}
package com.azhu.v2ex.http

import com.azhu.v2ex.http.cookie.CookieManager
import com.azhu.v2ex.utils.V2exUtils
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author: Jerry
 * @date: 2024-10-13 20:46
 * @version: 1.0.0
 */
class RequestHeaderInterceptor(private val cookieManager: CookieManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val uri = request.url
        if (V2exUtils.isIntraSiteLink(uri.toString())) {
            val cookies = cookieManager.loadForRequest(request.url)
            val sb = StringBuilder()
            cookies.forEachIndexed { index, cookie ->
                sb.append("${cookie.name}=${cookie.value}")
                if (index < cookies.size - 1) sb.append(";")
            }
            request.newBuilder()
                .addHeader("User-Agent", UserAgentUtils.UA)
                .addHeader("Cookie", sb.toString())
                .addHeader("Origin", "https://www.v2ex.com")
                .build()
        } else {
            request = request.newBuilder().build()
        }
        return chain.proceed(request)
    }

}
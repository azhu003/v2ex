package com.azhu.v2ex.http

import android.content.Context
import com.azhu.v2ex.http.cookie.CookieManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

/**
 * Retrofit配置
 * @author: azhu
 * @date: 2024-10-02 17:38
 * @version: 1.0.0
 */
object Retrofits {

    private const val TIME_OUT = 15L
    private lateinit var defaultRetrofit: Retrofit

    fun init(context: Context, baseUrl: String) {
        defaultRetrofit = create(context, baseUrl)
    }

    private fun create(context: Context, baseUrl: String): Retrofit {
        // OkHttp 提供的一个拦截器，用于记录和查看网络请求和响应的日志信息。
        val interceptor = HttpLoggingInterceptor()
        // 打印请求和响应的所有内容，响应状态码和执行时间等等。
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient()
            .newBuilder()
            .apply {
                addInterceptor(interceptor)
//                addInterceptor(TokenHeaderInterceptor())
                retryOnConnectionFailure(true)
                connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                readTimeout(TIME_OUT, TimeUnit.SECONDS)
                cookieJar(CookieManager(context))
            }
            .build()
        return Retrofit.Builder()
            .apply {
//                addConverterFactory(GsonConverterFactory.create())
                baseUrl(baseUrl)
                client(okHttpClient)
            }
            .build()
    }

    fun <T : Any> getService(clazz: KClass<T>): T {
        return getInstance().create(clazz.java)
    }

    private fun getInstance(): Retrofit {
        if (!Retrofits::defaultRetrofit.isInitialized) {
            throw NullPointerException("Retrofit is not initialized")
        }
        return defaultRetrofit
    }
}
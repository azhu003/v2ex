package com.azhu.v2ex.http

import android.content.Context
import coil.Coil
import coil.ImageLoader
import coil.memory.MemoryCache
import coil.request.CachePolicy
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

    lateinit var imageLoader: ImageLoader

    fun init(context: Context, baseUrl: String) {
        defaultRetrofit = create(context, baseUrl)
    }

    private fun create(context: Context, baseUrl: String): Retrofit {
        // OkHttp 提供的一个拦截器，用于记录和查看网络请求和响应的日志信息。
        val interceptor = HttpLoggingInterceptor()
        // 打印请求和响应的所有内容，响应状态码和执行时间等等。
        interceptor.level = HttpLoggingInterceptor.Level.HEADERS

        val cookieManager = CookieManager()
        val okHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(RequestHeaderInterceptor(cookieManager))
            .addInterceptor(RequestSchemeInterceptor())
            .addInterceptor(interceptor)
            .retryOnConnectionFailure(true)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .cookieJar(cookieManager)
            .hostnameVerifier { _, _ -> true }
            .build()

        setupCoil(context, okHttpClient)

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
    }

    private fun setupCoil(context: Context, okHttpClient: OkHttpClient) {
        imageLoader = ImageLoader.Builder(context)
            .memoryCache(MemoryCache.Builder(context).maxSizePercent(0.2).build())
            .diskCachePolicy(CachePolicy.ENABLED)  //磁盘缓策略 ENABLED、READ_ONLY、WRITE_ONLY、DISABLED
            .networkCachePolicy(CachePolicy.ENABLED)
            .crossfade(true) //淡入淡出
            .crossfade(500)  //淡入淡出时间
            .okHttpClient(okHttpClient)
            .build()
        Coil.setImageLoader(imageLoader)
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
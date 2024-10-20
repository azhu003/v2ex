package com.azhu.v2ex.http

import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody

/**
 * @author: azhu
 * @date: 2024-10-03 00:12
 * @version: 1.0.0
 */
class Http {

    val service: ApiService by lazy {
        Retrofits.getService(ApiService::class)
    }

    fun fetch(onRequestBefore: () -> Unit = {}, doRequest: suspend () -> ResponseBody) =
        flow<Result<ResponseBody>> {
            onRequestBefore.invoke()
            runCatching { doRequest.invoke() }
                .onSuccess { emit(Result.success(it)) }
                .onFailure { emit(Result.failure(ApiException(it.message))) }
        }

    fun <T> flows(onRequestBefore: () -> Unit = {}, doRequest: suspend () -> T) = flow<Result<T>> {
        onRequestBefore.invoke()
        runCatching { doRequest.invoke() }
            .onSuccess { emit(Result.success(it)) }
            .onFailure { emit(Result.failure(if (it is ApiException) it else ApiException(it.message))) }
    }
}
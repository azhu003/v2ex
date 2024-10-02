package com.azhu.v2ex.http

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
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

    fun fetch(doRequest: suspend () -> ResponseBody) =
        flow<SimpleResponse<ResponseBody>> {
            emit(SimpleResponse.Before)
            runCatching { doRequest.invoke() }
                .onSuccess {
                    emit(SimpleResponse.Success(it))
                }
                .onFailure {
                    emit(SimpleResponse.Error(ApiException(it.message)))
                }
        }
}

fun <T> Flow<SimpleResponse<T>>.before(onBefore: suspend () -> Unit): Flow<SimpleResponse<T>> {
    return onEach {
        if (it == SimpleResponse.Before) {
            onBefore.invoke()
        }
    }
}

fun <T> Flow<SimpleResponse<T>>.success(onSuccess: suspend (T) -> Unit): Flow<SimpleResponse<T>> {
    return onEach {
        if (it is SimpleResponse.Success) {
            onSuccess.invoke(it.data)
        }
    }
}

fun <T> Flow<SimpleResponse<T>>.error(onError: suspend (ApiException) -> Unit): Flow<SimpleResponse<T>> {
    return onEach {
        if (it is SimpleResponse.Error) {
            onError.invoke(it.exception)
        }
    }
}
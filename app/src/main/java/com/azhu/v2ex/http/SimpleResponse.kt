package com.azhu.v2ex.http

/**
 * @author: azhu
 * @date: 2024-10-03 01:16
 * @version: 1.0.0
 */
sealed class SimpleResponse<out T> {
    data object Before : SimpleResponse<Nothing>()
    data class Success<out T>(val data: T) : SimpleResponse<T>()
    data class Error<out T>(val exception: ApiException) : SimpleResponse<T>()
}
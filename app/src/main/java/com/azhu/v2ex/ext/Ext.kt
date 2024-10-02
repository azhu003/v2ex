package com.azhu.v2ex.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.azhu.basic.provider.logger
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.reflect.KClass

/**
 * @description:
 * @author: azhu
 * @date: 2024-10-02 00:16
 * @version: 1.0.0
 */
fun <T : Activity> Context.startActivityClass(target: KClass<T>) {
    startActivity(Intent(this, target.java))
}


fun Any?.toJson(): String {
    return getJacksonMapper().writeValueAsString(this)
}

fun <T : Any> String?.fromJson(clazz: KClass<T>): T? {
    return try {
        getJacksonMapper().readValue(this, clazz.java)
    } catch (e: Exception) {
        logger.error("JSON反序列化失败：$this  $clazz", e)
        null
    }
}

fun <T : Any> String?.fromJsonArray(clazz: KClass<T>): List<T>? {
    return try {
        getJacksonMapper().readValue(this, object : TypeReference<List<T>>() {})
    } catch (e: Exception) {
        logger.error("JSON反序列化失败：$this  $clazz", e)
        null
    }
}

private fun getJacksonMapper(): ObjectMapper {
    return ObjectMapper()
}
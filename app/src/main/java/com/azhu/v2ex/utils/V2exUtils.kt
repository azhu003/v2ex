package com.azhu.v2ex.utils

/**
 * @author: azhu
 * @date: 2024-10-10 19:35
 * @version: 1.0.0
 */
object V2exUtils {

    fun isMemberUrl(path: String): Boolean {
        return path.startsWith("/member")
    }

    fun isRelativeURL(path: String): Boolean {
        return isMemberUrl(path)
    }

}
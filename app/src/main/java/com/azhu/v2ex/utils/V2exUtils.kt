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

    fun isSubjectUrl(path: String): Boolean {
        return path.startsWith("/t")
    }

    fun isRelativeURL(path: String): Boolean {
        return isMemberUrl(path) || isSubjectUrl(path)
    }

    fun isIntraSiteLink(url: String): Boolean {
        return url.startsWith("https://www.v2ex.com") || url.startsWith("https://v2ex.com")
    }

    fun getRelativeURL(url: String): String {
        return url.removePrefix("https://www.v2ex.com").removePrefix("https://v2ex.com")
    }

}
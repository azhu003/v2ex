package com.azhu.v2ex.utils

import android.net.Uri
import com.azhu.basic.AppManager
import com.azhu.basic.provider.StoreProvider
import com.azhu.v2ex.http.cookie.PersistentCookieStore

/**
 * @author: azhu
 * @date: 2024-10-10 19:35
 * @version: 1.0.0
 */
object V2exUtils {

    fun isMemberUrl(path: String): Boolean {
        return path.startsWith("/member")
    }

    fun isTopicUrl(path: String): Boolean {
        return path.startsWith("/t")
    }

    fun isRelativeURL(path: String): Boolean {
        return try {
            Uri.parse(path).isRelative
        } catch (e: Exception) {
            false
        }
    }

    fun isIntraSiteLink(url: String): Boolean {
        return url.startsWith("https://www.v2ex.com") || url.startsWith("https://v2ex.com")
    }

    fun getRelativeURL(url: String): String {
        return url.removePrefix("https://www.v2ex.com").removePrefix("https://v2ex.com")
    }

    fun toAbsoluteURL(relativeURL: String): String {
        return "https://www.v2ex.com$relativeURL"
    }

    fun isLogged(): Boolean {
        return StoreProvider.getBool(Constant.LOGGED_KEY)
    }

    fun login(username: String) {
        StoreProvider.save(Constant.CURRENT_LOGGED_USER, username)
        StoreProvider.save(Constant.LOGGED_KEY, true)
    }

    fun logout() {
        val context = AppManager.getCurrentActivity()
        if (context != null) {
            PersistentCookieStore.removeAll()
            StoreProvider.remove(Constant.CURRENT_LOGGED_USER)
            StoreProvider.remove(Constant.LOGGED_KEY)
        }
    }

    fun getCurrentUsername(): String? {
        return StoreProvider.getString(Constant.CURRENT_LOGGED_USER)
    }

    fun fixUrl(url: String): String {
        if (url.startsWith("//")) {
            return "https:$url"
        }
        return url
    }
}
package com.azhu.v2ex.http.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * @description:
 * @author: azhu
 * @date: 2024-10-02 18:35
 * @version: 1.0.0
 */
class CookieManager : CookieJar {

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return PersistentCookieStore.get(url)
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isNotEmpty()) {
            for (item in cookies) {
                PersistentCookieStore.add(url, item)
            }
        }
    }
}
package com.azhu.v2ex.http

/**
 * @author: Jerry
 * @date: 2024-10-31 16:31
 * @version: 1.0.0
 */
enum class Referer(private val destination: String, private val referer: String) {
    Signin("/signin", "/signin?next=/mission/daily"),
    SignOut("/signout", "/member/{username}"),
    Mission("/mission/daily/redeem", "/signin?next=/mission/daily"),
    Favorite("/favorite/topic/", "/t/{tid}"),
    UnFavorite("/unfavorite/topic/", "/t/{tid}"),
    Ignore("/ignore/topic/", "/t/{tid}"),
    Thank("/thank/topic/", "/t/{tid}"),
    ;

    fun getReferer(key: String, value: String): String {
        return referer.replace("{$key}", value)
    }

}
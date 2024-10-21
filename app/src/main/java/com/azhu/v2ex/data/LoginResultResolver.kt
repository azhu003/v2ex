package com.azhu.v2ex.data

import com.azhu.v2ex.http.ApiException
import com.azhu.v2ex.utils.V2exUtils
import org.jsoup.nodes.Document

/**
 * @author: Jerry
 * @date: 2024-10-20 04:41
 * @version: 1.0.0
 */
class LoginResultResolver : BaseResolver<Any>() {

    override fun resolver(document: Document): Any {
        val error = str { document.select("div.problem li").text() }
        if (error.isNotEmpty()) {
            throw ApiException(error)
        } else {
            val username = document.select("div#Rightbar a[href^=/member/]").text()
            if (username.isNotEmpty()) {
                V2exUtils.login(username)
            }
        }
        return Unit
    }
}
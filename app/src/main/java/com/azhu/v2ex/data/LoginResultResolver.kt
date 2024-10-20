package com.azhu.v2ex.data

import com.azhu.v2ex.http.ApiException
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
        }
        return Unit
    }
}
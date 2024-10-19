package com.azhu.v2ex.data

import org.jsoup.nodes.Document

/**
 * @author: Jerry
 * @date: 2024-10-20 04:41
 * @version: 1.0.0
 */
class LoginResultResolver : BaseResolver<LoginResult>() {
    override fun resolver(document: Document): LoginResult {

        return LoginResult()
    }
}
package com.azhu.v2ex.data

import com.azhu.basic.provider.logger
import org.jsoup.nodes.Document

/**
 * @author: Jerry
 * @date: 2024-10-21 21:29
 * @version: 1.0.0
 */
class LogoutResolver : BaseResolver<Any>() {
    override fun resolver(document: Document): Any {
        val div = document.select("div#Main > div.box > div.inner")
        val isSuccessful = div.text().contains("你已经完全登出")
        if (!isSuccessful) {
            logger.info("登出请求失败 ${div.text()}")
        }
        return Unit
    }
}
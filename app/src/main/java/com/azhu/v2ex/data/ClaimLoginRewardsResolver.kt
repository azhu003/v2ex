package com.azhu.v2ex.data

import com.azhu.v2ex.http.KnownApiException
import org.jsoup.nodes.Document

/**
 * @author: Jerry
 * @date: 2024-10-21 01:20
 * @version: 1.0.0
 */
class ClaimLoginRewardsResolver : BaseResolver<Any>() {

    override fun resolver(document: Document): Any {
        val isSucceed = document.select("div#Main div.message li").text().contains("已成功领取每日登录奖励")
        if (!isSucceed) {
            val message = document.select("div#Main div.box > div:nth-child(2)").last()?.text()
            throw KnownApiException(message ?: "领取失败")
        }
        return Unit
    }
}
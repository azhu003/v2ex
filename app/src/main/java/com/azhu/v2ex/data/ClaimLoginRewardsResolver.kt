package com.azhu.v2ex.data

import com.azhu.v2ex.utils.Constant
import org.jsoup.nodes.Document

/**
 * @author: Jerry
 * @date: 2024-10-21 01:20
 * @version: 1.0.0
 */
class ClaimLoginRewardsResolver : BaseResolver<JsonResult>() {

    override fun resolver(document: Document): JsonResult {
        val isSucceed = document.select("div#Main div.message li").text().contains("已成功领取每日登录奖励")
        var message: String? = null
        if (!isSucceed) {
            message = document.select("div#Main div.box > div:nth-child(2)").last()?.text()
        }
        val input = document.select("div#Main div.box input[onclick^=location.href]").attr("onclick")
        val once = Constant.ONCE.find(input)?.value
        return JsonResult(success = isSucceed, message = message, once = once)
    }
}
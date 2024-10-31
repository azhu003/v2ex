package com.azhu.v2ex.data

import com.azhu.v2ex.utils.Constant
import org.jsoup.nodes.Document
import kotlin.math.pow

/**
 * @author: Jerry
 * @date: 2024-10-19 14:22
 * @version: 1.0.0
 */
class UserProfileResolver : BaseResolver<UserProfile>() {

    override fun resolver(document: Document): UserProfile {
        val isUnlogged = document.select("div#Main div.message").text().contains("你要查看的页面需要先登录")
        val profile = UserProfile(isUnlogged)
        if (!isUnlogged) {
            val rightDiv = document.select("div#Rightbar")
            profile.username = str { rightDiv.select("a[href^=/member/]").text() }
            profile.avatar = str { rightDiv.select("img.avatar[src]").attr("src") }
            profile.numberOfNodeCollection = str { rightDiv.select("a[href^=/my/nodes] span.bigger").text() }.toIntOrNull()
            profile.numberOfTopicCollection = str { rightDiv.select("a[href^=/my/topics] span.bigger").text() }.toIntOrNull()
            profile.numberOfFollowing = str { rightDiv.select("a[href^=/my/following] span.bigger").text() }.toIntOrNull()

            val boxDiv = document.select("div#Main div.box")
            profile.daysOfConsecutiveLogin = str {
                Constant.DAYS_OF_CONSECUTIVE_LOGIN.find(boxDiv.select("div.cell").last()?.text() ?: "")?.value
            }.toIntOrNull()
            profile.isClaimedLoginRewards = document.select("div#Main div.box").text().contains("每日登录奖励已领取")
            profile.balance = str {
                val balanceStr = rightDiv.select("div#money a").text().trim()
                val balanceArray = balanceStr.split(" ").reversed()
                var balance = 0
                balanceArray.forEachIndexed { index, b -> balance += b.toInt() * 100.0.pow(index).toInt() }
                return@str "$balance"
            }
            if (!profile.isClaimedLoginRewards) {
                val input = document.select("div#Main div.box input[onclick^=location.href]").attr("onclick")
                profile.claimedLoginRewardNonce = Constant.ONCE.find(input)?.value
            }
        }
        return profile
    }

}
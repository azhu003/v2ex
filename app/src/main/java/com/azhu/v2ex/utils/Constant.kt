package com.azhu.v2ex.utils

/**
 * @author: azhu
 * @date: 2024-10-06 00:53
 * @version: 1.0.0
 */
object Constant {

    const val LOGGED_KEY = "logged"
    const val CURRENT_LOGGED_USER = "current_logged_user"

    //主题ID /t/1077894#reply5
    val TOPIC_ID by lazy { Regex("(?<=/t/)\\d{1,8}") }

    //点击数 · 598 次点击
    val CLICKS by lazy { Regex("(?<=\\s·\\s)\\d*?(?=\\s*次点击)") }

    //收藏数 "598 次点击 &nbsp;∙&nbsp; 11 人收藏 &nbsp; ∙&nbsp; 5 人感谢"
    val COLLECTIONS by lazy { Regex("(?<=∙&nbsp;\\s).*?(?=\\s*人收藏)") }

    //感谢数
    val USEFUL by lazy { Regex("(?<=人收藏 &nbsp; ∙&nbsp;\\s).*?(?=\\s*人感谢)") }

    //主题回复ID
    val REPLY_ID by lazy { Regex("(?<=r_)\\d+") }

    //回复数量 "133 条回复"
    val REPLY_NUMBERS by lazy { Regex("(?<=\\s{0,5}).*?(?=\\s条回复)") }

    //用户名 "/member/username"
    val MEMBER_USERNAME by lazy { Regex("(?<=/member/).*") }

    //V2EX 第 552124 号会员，加入于 2021-07-30 15:11:14 +08:00
    val MEMBER_NO by lazy { Regex("(?<=第\\s).*?(?=\\s号会员)") }
    val REGISTER_AT by lazy { Regex("(?<=加入于\\s).*?(?=\\s+今日活跃度)") }

    //获取节点名"/go/rss"
    val NODE_NAME by lazy { Regex("(?<=/go/).*") }

    //连续登录天数
    val DAYS_OF_CONSECUTIVE_LOGIN by lazy { Regex("(?<=已连续登录\\s)\\d*?(?=\\s天)") }

    //领取每日奖励once ClaimedLoginRewards
    val CLAIMED_LOGIN_REWARD_ONCE by lazy { Regex("(?<=once=)\\d*") }

}
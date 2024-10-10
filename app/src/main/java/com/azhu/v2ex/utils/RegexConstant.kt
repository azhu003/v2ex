package com.azhu.v2ex.utils

/**
 * @author: azhu
 * @date: 2024-10-06 00:53
 * @version: 1.0.0
 */
object RegexConstant {

    //主题ID /t/1077894#reply5
    val TOPIC_ID by lazy { Regex("(?<=/t/).*?(?=#reply)") }

    //点击数 · 598 次点击
//    val CLICKS by lazy { Regex("(?<=·\\s).*?(?=\\s次点击)") }
    val CLICKS by lazy { Regex("(?<=[0前]\\s·\\s).*?(?=\\s*次点击)") }

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
}
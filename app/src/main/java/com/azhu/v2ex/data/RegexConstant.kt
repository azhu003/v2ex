package com.azhu.v2ex.data

/**
 * @author: azhu
 * @date: 2024-10-06 00:53
 * @version: 1.0.0
 */
object RegexConstant {

    //主题ID /t/1077894#reply5
    val TOPIC_ID by lazy { Regex("(?<=/t/).*?(?=#reply)") }

}
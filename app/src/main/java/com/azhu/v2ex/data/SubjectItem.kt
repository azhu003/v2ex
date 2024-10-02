package com.azhu.v2ex.data

import androidx.compose.runtime.Stable

@Stable
data class SubjectItem(
    val id: String = "",
    val title: String = "",
    val node: String = "",
    val operator: String = "", //作者
    val avatar: String = "", //作者头像
    val time: String = "", //发布时间
    val replies: Int = 0  //回复总数
)
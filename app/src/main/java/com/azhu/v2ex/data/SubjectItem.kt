package com.azhu.v2ex.data

import androidx.compose.runtime.Stable

@Stable
data class SubjectItem(
    var id: String = "",
    var title: String = "",
    var node: String = "",
    var operator: String = "", //作者
    var avatar: String = "", //作者头像
    var time: String= "", //发布时间
    var replies: Int? = 0  //回复总数
)
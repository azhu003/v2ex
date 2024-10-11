package com.azhu.v2ex.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

@Stable
data class NodeItem(val key: String = "", val name: String = "")

@Stable
data class HomePageState(
    var index: Int = 0
)

@Stable
data class TabPageState(
    val node: NodeItem = NodeItem(),
    val subjects: SnapshotStateList<SubjectItem> = mutableStateListOf(),
)

@Stable
data class SubjectItem(
    var id: String = "",
    var title: String = "",
    var node: String = "",
    var operator: String = "", //作者
    var avatar: String = "", //作者头像
    var time: String = "", //发布时间
    var replies: Int? = 0  //回复总数
)

@Stable
data class SubjectDetails(
    var sid: String? = null,
//    val title: MutableState<String> = mutableStateOf(""),  //标题
//    val author: MutableState<String> = mutableStateOf(""),
//    val time: MutableState<String> = mutableStateOf(""),  //发布时间
//    val content: MutableState<String> = mutableStateOf(""),
//    val clicks: MutableState<String> = mutableStateOf(""),  //x次点击
//    val collections: MutableState<String> = mutableStateOf(""),  //收藏次数
//    val useful: MutableState<String> = mutableStateOf(""),  //x人感谢

//    val reply: SnapshotStateList<SubjectReplyItem> = mutableStateListOf(),

    var title: String = "",  //标题
    var author: String = "",
    var time: String = "",  //发布时间
    var content: String = "",
    var clicks: String = "",  //x次点击
    var collections: String = "",  //收藏次数
    var useful: String = "",  //x人感谢
    var replyCount: String = "",
    val reply: MutableList<SubjectReplyItem> = mutableListOf()
)

@Stable
data class SubjectReplyItem(
    var id: String = "",
    var username: String = "",
    var avatar: String = "",
    var time: String = "",
    var isAuthor: Boolean = false,
    var content: String = "",
    var star: String? = null
)
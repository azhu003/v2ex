package com.azhu.v2ex.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap

@Stable
data class NodeInfo(val key: String = "", val name: String = "")

@Stable
data class HomePageState(
    var index: Int = 0,
    //用于更新刷新状态，值与TabPagerState中isRefreshing保持一致
    val isRefreshing: MutableState<Boolean> = mutableStateOf(false),
    val nodes: SnapshotStateList<NodeInfo> = mutableStateListOf(),
    val tabPageMap: SnapshotStateMap<Int, TabPageState> = mutableStateMapOf()
)

@Stable
data class TabPageState(
    val node: NodeInfo = NodeInfo(),
    val isRefreshing: MutableState<Boolean> = mutableStateOf(false),
    var isFirstVisible: Boolean = true,
    val subjects: SnapshotStateList<SubjectItem> = mutableStateListOf(),
)

@Stable
data class SubjectItem(
    var id: String = "",
    var title: String = "",
    var node: String = "",
    var author: String = "", //作者
    var avatar: String = "", //作者头像
    var time: String = "", //发布时间
    var replies: Int? = 0  //回复总数
)

@Stable
data class SubjectDetails(
    var sid: String? = null,
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

@Stable
data class UserDetails(
    var username: String = "",
    var avatar: String = "",
    var online: Boolean = false,
    var no: String = "", //注册序号
    var registerAt: String = "", //注册时间
    var ranking: String = "", //排名
    var subjects: MutableList<UserRecentlySubject> = mutableListOf(),
    var subjectHidden: Boolean = false,
    var replys: MutableList<UserRecentlyReply> = mutableListOf()
)

//最近发布的主题
@Stable
data class UserRecentlySubject(
    var sid: String = "",
    var title: String = "",
    var node: NodeInfo = NodeInfo(),
    var time: String = "",
    var replyCount: String = "",
)

//最近回复列表
@Stable
data class UserRecentlyReply(
    //主题作者
    var author: String = "",
    //主题标题
    var subject: String = "",
    //主题id
    var sid: String = "",
    //节点信息
    var node: NodeInfo = NodeInfo(),
    //回复内容
    var content: String = "",
    //回复时间
    var time: String = "",
)
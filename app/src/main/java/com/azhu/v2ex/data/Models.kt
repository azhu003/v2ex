package com.azhu.v2ex.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList

@Stable
data class NavigationItem(
    @StringRes
    val label: Int,
    @DrawableRes
    val icon: Int,
    val route: String
)

@Stable
data class TabPair(val key: String = "", val name: String = "")

/**
 * 分页数据
 * @author: Jerry
 * @date: 2024/10/17 01:06
 * @version: v1.0
 */
@Stable
data class Pagination<T>(
    var page: Int = 1,
    var total: Int = 1,
    val data: SnapshotStateList<T> = mutableStateListOf()
) {
    val hasNext: MutableState<Boolean> = mutableStateOf(page < total)
    val nextPage: Int get() = if (page < total) page + 1 else 1
}

@Stable
data class Topic(
    var id: String = "",
    var title: String = "",
    var node: String = "",
    var author: String = "", //作者
    var avatar: String = "", //作者头像
    var time: String = "", //发布时间
    var replies: Int? = 0  //回复总数
)

@Stable
data class TopicDetails(
    var isInitialized: Boolean = false,
    var tid: String? = null,
    var title: String = "",  //标题
    var author: String = "",
    var time: String = "",  //发布时间
    var content: String = "",
    var subtitles: MutableList<TopicDetailsSubtitle> = mutableListOf(), //附言
    var clicks: String = "",  //x次点击
    var collections: String = "",  //收藏次数
    var useful: String = "",  //x人感谢
    var replyCount: String = "",

    var replys: Pagination<TopicReplyItem> = Pagination(),
)

@Stable
enum class TopicDetailsResolverType {
    ONLY_REPLY,
    ONLY_TOPIC_BODY,
    ALL
}

@Stable
data class TopicDetailsSubtitle(
    var time: String = "",
    var content: String = ""
)

@Stable
data class TopicReplyItem(
    var id: String = "",
    var username: String = "",
    var avatar: String = "",
    var time: String = "",
    var no: String? = null,
    var badges: MutableList<String> = mutableListOf(),
    var content: String = "",
    var star: String? = null,
)

@Stable
data class UserDetails(
    var username: String = "",
    var avatar: String = "",
    var online: Boolean = false,
    var no: String = "", //注册序号
    var registerAt: String = "", //注册时间
    var ranking: String = "", //排名
    var topics: SnapshotStateList<UserRecentlyTopic> = mutableStateListOf(),
    var topicInvisible: Boolean = false,
    var replys: SnapshotStateList<UserRecentlyReply> = mutableStateListOf()
)

//最近发布的主题
@Stable
data class UserRecentlyTopic(
    var sid: String = "",
    var title: String = "",
    var node: TabPair = TabPair(),
    var time: String = "",
    var replyCount: String = "",
)

//最近回复列表
@Stable
data class UserRecentlyReply(
    //主题作者
    var author: String = "",
    //主题标题
    var topic: String = "",
    //主题id
    var sid: String = "",
    //节点信息
    var node: TabPair = TabPair(),
    //回复内容
    var content: String = "",
    //回复时间
    var time: String = "",
)

@Stable
data class UserProfile(
    val isUnlogged: Boolean,
    var username: String = "",
    var avatar: String = "",
    var numberOfNodeCollection: Int? = 0,
    var numberOfTopicCollection: Int? = 0,
    var numberOfFollowing: Int? = 0,
    /**
     * 是否已经领取过登录奖励 已领取: true 未领取: false
     */
    var isClaimedLoginRewards: Boolean = false,
    /**
     * 领取登录奖励的Nonce，已领取过时值为空
     */
    var claimedLoginRewardNonce: String? = null,
    var daysOfConsecutiveLogin: Int? = 0,
    var balance: String = "",

    var topicInvisible: Boolean = false,
    var topics: SnapshotStateList<UserRecentlyTopic> = mutableStateListOf(),
    var replys: SnapshotStateList<UserRecentlyReply> = mutableStateListOf()
)

@Stable
data class NodeInfo(
    var key: String = "",
    var name: String = "",
    var image: String = "",
    var comments: String = "",
)

@Stable
data class LoginUiState(
    val isLoading: MutableState<Boolean> = mutableStateOf(false)
)

//登录时的动态参数名
@Stable
class LoginRequestParams {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var captchaImageUrl by mutableStateOf("")
    var captcha by mutableStateOf("")
    var nonce by mutableStateOf("")
}

@Stable
data class SettingsState(
    var isLogoutSuccessfully: MutableState<Boolean> = mutableStateOf(false)
)

@Stable
data class TopicByNode(
    var nodeName: String = "",
    var nodeImage: String = "",
    var intro: String = "",
    var comments: String = "",
    var isCollected: Boolean = false,
    var pagination: Pagination<Topic> = Pagination()
)

@Stable
data class NodeNav(
    var key: String = "",
    var label: String = "",
)
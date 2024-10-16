package com.azhu.v2ex.data

import androidx.compose.runtime.Stable

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
    val data: MutableList<T> = mutableListOf()
) {
    fun hasNext(): Boolean {
        return page < total
    }
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
    var sid: String? = null,
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

    val reply: MutableList<TopicReplyItem> = mutableListOf()
)

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
    var isAuthor: Boolean = false,
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
    var topics: MutableList<UserRecentlyTopic> = mutableListOf(),
    var topicInvisible: Boolean = false,
    var replys: MutableList<UserRecentlyReply> = mutableListOf()
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
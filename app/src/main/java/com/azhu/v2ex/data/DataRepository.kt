package com.azhu.v2ex.data

import com.azhu.basic.AppManager
import com.azhu.basic.provider.logger
import com.azhu.v2ex.http.Http
import com.azhu.v2ex.http.Referer
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

/**
 * @author: Jerry
 * @date: 2024-10-16 02:56
 * @version: 1.0.0
 */
class DataRepository private constructor() {

    private val remote = Http()

    companion object {
        val INSTANCE: DataRepository by lazy { DataRepository() }
    }

    suspend fun getTopicListData(tabName: String): Pagination<Topic> {
//        val body = getHtmlFromAssets("topics.html")
        val body = remote.service.getTopicList(tabName).byteStream()
        return TopicListResolver().resolver(body)
    }

    suspend fun getRecentTopicList(page: Int = 1): Pagination<Topic> {
//        val body = getHtmlFromAssets("topics.html")
        val body = remote.service.getRecentTopicList(page).byteStream()
        return TopicListResolver().resolver(body)
    }

    suspend fun getTopicDetails(
        tid: String,
        type: TopicDetailsResolverType = TopicDetailsResolverType.ALL,
        page: Int = 1
    ): TopicDetails {
//        val body = getHtmlFromAssets("topic.html")
        val body = remote.service.getTopicDetails(tid, page).byteStream()
        return TopicDetailsResolver(type, tid).resolver(body)
    }

    suspend fun favorite(tid: String, once: String): TopicDetails {
        val body = remote.service.favorite(tid, once, Referer.Favorite.getReferer("tid", tid)).byteStream()
        return TopicDetailsResolver(TopicDetailsResolverType.ONLY_TOPIC_BODY, tid).resolver(body)
    }

    suspend fun unfavorite(tid: String, once: String): TopicDetails {
        val body = remote.service.unfavorite(tid, once, Referer.UnFavorite.getReferer("tid", tid)).byteStream()
        return TopicDetailsResolver(TopicDetailsResolverType.ONLY_TOPIC_BODY, tid).resolver(body)
    }

    suspend fun ignore(tid: String, once: String): TopicDetails {
        val body = remote.service.ignore(tid, once, Referer.Ignore.getReferer("tid", tid)).byteStream()
        return TopicDetailsResolver(TopicDetailsResolverType.ONLY_TOPIC_BODY, tid).resolver(body)
    }

    suspend fun thank(tid: String, once: String): JsonResult {
        val body = remote.service.thank(tid, once).string()
        val result = toJsonResult(body)
        return result
    }

    suspend fun thankReplay(rid: String, once: String): JsonResult {
        val body = remote.service.thankReply(rid, once).string()
        val result = toJsonResult(body)
        logger.i("thank has sent. result: rid=$rid once=$once -> $result")
        return result
    }

    suspend fun reply(tid: String, content: String, once: String): TopicDetails {
        val form = mapOf(Pair("once", once), Pair("content", content), Pair("return_to_page", "1"))
        val body = remote.service.replyTopic(tid, form).byteStream()
        return TopicDetailsResolver(TopicDetailsResolverType.ALL, tid).resolver(body)
    }

    suspend fun getUserDetails(username: String): UserDetails {
//        val body = getHtmlFromAssets("member.html")
        val body = remote.service.getMemberDetails(username).byteStream()
        return UserDetailsResolver().resolver(body)
    }

    suspend fun getUserProfile(): UserProfile {
//        val body = getHtmlFromAssets("profile.html")
        val body = remote.service.getUserProfile().byteStream()
        return UserProfileResolver().resolver(body)
    }

    suspend fun getSigninParams(): LoginRequestParams {
//        val body = getHtmlFromAssets("signin.html")
        val body = remote.service.getSigninParams().byteStream()
        return LoginBeforeParamsResolver().resolver(body)
    }

    suspend fun signin(
        username: Pair<String, String>,
        password: Pair<String, String>,
        captcha: Pair<String, String>,
        nonce: String
    ): Any {
        val form = mutableMapOf<String, String>()
        form[username.first] = username.second
        form[password.first] = password.second
        form[captcha.first] = captcha.second
        form["once"] = nonce
        form["next"] = "/mission/daily"
//        val body = getHtmlFromAssets("signin_failed.html")
        val body = remote.service.signin(form).byteStream()
        return LoginResultResolver().resolver(body)
    }

    suspend fun getDayMission(once: String): JsonResult {
//        delay(Random.nextLong(2000, 3000))
//        val body = getHtmlFromAssets("daily.html")
        val body = remote.service.claimLoginRewards(once).byteStream()
        return ClaimLoginRewardsResolver().resolver(body)
    }

    suspend fun logout(username: String, once: String): Any {
        val body = remote.service.logout(once, Referer.SignOut.getReferer("username", username)).byteStream()
        return LogoutResolver().resolver(body)
    }

    suspend fun getNodesFromCollection(): List<NodeInfo> {
        val body = remote.service.getNodesFromCollection().byteStream()
        return NodeCollectionResolver().resolver(body)
    }

    suspend fun getTopicsFromCollection(): List<Topic> {
        val body = remote.service.getTopicsFromCollection().byteStream()
        val pagination = TopicListResolver().resolver(body)
        return pagination.data
    }

    suspend fun getFollowings(page: Int = 1): Pagination<Topic> {
        val body = remote.service.getFollowing(page).byteStream()
        return TopicListResolver().resolver(body)
    }

    suspend fun getAllTopicByUser(username: String, page: Int = 1): Pagination<Topic> {
        val body = remote.service.getAllTopicsByUser(username, page).byteStream()
        return TopicListResolver().resolver(body)
    }

    suspend fun getAllRepliesByUser(username: String, page: Int = 1): Pagination<UserRecentlyReply> {
        val body = remote.service.getAllRepliesByUser(username, page).byteStream()
        return RepliesByUserResolver().resolver(body)
    }

    suspend fun getTopicsByNode(
        node: String,
        page: Int = 1,
        onlyNodeInfo: Boolean = false,
        onlyTopic: Boolean = false
    ): TopicByNode {
        val body = remote.service.getTopicsByNode(node, page).byteStream()
        return TopicsByNodeResolver(onlyNodeInfo, onlyTopic).resolver(body)
    }

    suspend fun getNodeNavigation(): Map<String, List<NodeNav>> {
        val body = remote.service.getNodeNavigation().byteStream()
//        val body = getHtmlFromAssets("topics.html")
        return NodeNavigationResolver().resolver(body)
    }

    private fun toJsonResult(body: String): JsonResult {
        try {
            val json = JSONObject(body)
            val result = JsonResult()
            result.success = json.optBoolean("success", false)
            val once = json.optInt("once", -1)
            result.once = if (once == -1) null else "$once"
            val message = json.optString("message", "")
            result.message = message.ifEmpty { null }
            return result
        } catch (e: Exception) {
            logger.e("json 解析失败: $e\n$body")
            return lazy { JsonResult(success = false, message = "error") }.value
        }
    }

     fun getHtmlFromAssets(fileName: String): InputStream {
        val context = AppManager.getCurrentActivity()
        var input: InputStream? = null
        if (context != null) {
            input = context.resources.assets.open(fileName)
        }
        if (input == null) throw IOException("读取assets文件失败")
        return input
    }
}
package com.azhu.v2ex.data

import com.azhu.basic.AppManager
import com.azhu.v2ex.http.Http
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
        val body = remote.service.getRecentTopicList(page).byteStream()
        return TopicListResolver().resolver(body)
    }

    suspend fun getTopicDetails(tid: String): TopicDetails {
//        val body = getHtmlFromAssets("topic.html")
        val body = remote.service.getTopicDetails(tid).byteStream()
        return TopicDetailsResolver().resolver(body)
    }

    suspend fun getUserDetails(username: String): UserDetails {
//        val body = getHtmlFromAssets("member.html")
        val body = remote.service.getMemberDetails(username).byteStream()
        return UserDetailsResolver().resolver(body)
    }

    private fun getHtmlFromAssets(fileName: String): InputStream {
        val context = AppManager.getCurrentActivity()
        var input: InputStream? = null
        if (context != null) {
            try {
                input = context.resources.assets.open(fileName)
            } catch (e: Exception) {
                throw e
            } finally {
                input?.close()
            }
        }
        if (input == null) throw IOException("读取assets文件失败")
        return input
    }
}
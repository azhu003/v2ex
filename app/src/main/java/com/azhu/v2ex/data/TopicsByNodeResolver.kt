package com.azhu.v2ex.data

import com.azhu.v2ex.utils.Constant
import com.azhu.v2ex.utils.DateTimeUtils
import org.jsoup.nodes.Document

/**
 * @author: Jerry
 * @date: 2024-10-23 17:01
 * @version: 1.0.0
 */
class TopicsByNodeResolver(private val onlyNodeInfo: Boolean = false, private val onlyTopic: Boolean = false) :
    BaseResolver<TopicByNode>() {

    override fun resolver(document: Document): TopicByNode {
        val data = TopicByNode()
        if (onlyNodeInfo) {
            data.nodeImage = str { document.select("div.page-content-header > img").attr("src") }
            data.nodeName = str {
                val breadcrumb = document.select("div.page-content-header div.node-breadcrumb").text()
                breadcrumb.split("›").last().trim()
            }
            data.comments = str { document.select("div.page-content-header span.topic-count > strong").text() }
            data.intro = str { document.select("div.page-content-header div.intro").text() }
        } else if (onlyTopic) {
            val pagination = Pagination<Topic>()
            val elements = document.select("div#TopicsNode tr")
            for (tr in elements) {
                catch {
                    val topic = Topic()
                    topic.avatar = str { tr.select("img.avatar").attr("src") }
                    val topicInfo = tr.children()[2]  // <span class="topic_info">
                    val a = topicInfo.select("span.item_title > a")
                    topic.id = str { Constant.TOPIC_ID.find(a.attr("href"))!!.value }
                    topic.title = str { a.text() }
                    topic.author = str { topicInfo.select("a[href^=/member/]").first()?.text() ?: "" }
                    topic.time = str { DateTimeUtils.ago(topicInfo.select("span[title]").attr("title")) }
                    topic.replies = str { tr.select("a.count_livid").text() }.toIntOrNull()
                    pagination.data.add(topic)
                }
            }
            //分页数据
            val pages = document.select("div.ps_container").first()?.select("a[href]")
            pagination.page = pages?.select("a.page_current")?.text()?.toIntOrNull() ?: 1
            pagination.total = pages?.last()?.text()?.toIntOrNull() ?: 1
            data.pagination = pagination
        }
        return data
    }
}
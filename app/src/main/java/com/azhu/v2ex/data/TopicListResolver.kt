package com.azhu.v2ex.data

import com.azhu.v2ex.utils.DateTimeUtils
import com.azhu.v2ex.utils.Constant
import org.jsoup.nodes.Document

/**
 * 主题列表解析
 * @author: Jerry
 * @date: 2024-10-16 03:29
 * @version: 1.0.0
 */
class TopicListResolver : BaseResolver<Pagination<Topic>>() {

    override fun resolver(document: Document): Pagination<Topic> {
        val pagination = Pagination<Topic>()
        val elements = document.select("div.cell.item tr")
        for (tr in elements) {
            catch {
                val topic = Topic()
                topic.avatar = str { tr.select("img.avatar").attr("src") }
                val topicInfo = tr.select("span.topic_info")  // <span class="topic_info">
                val a = tr.select("span.item_title > a[href^=/t/]")
                topic.id = str { Constant.TOPIC_ID.find(a.attr("href"))!!.value }
                topic.title = str { a.text() }
                topic.node = str { topicInfo.select("a.node").text() }
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
        return pagination
    }
}
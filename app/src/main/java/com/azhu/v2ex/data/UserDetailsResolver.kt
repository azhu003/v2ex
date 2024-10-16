package com.azhu.v2ex.data

import com.azhu.basic.provider.logger
import com.azhu.v2ex.utils.DateTimeUtils
import com.azhu.v2ex.utils.RegexConstant
import org.jsoup.nodes.Document

/**
 * @author: Jerry
 * @date: 2024-10-17 01:22
 * @version: 1.0.0
 */
class UserDetailsResolver : BaseResolver<UserDetails>() {

    override fun resolver(document: Document): UserDetails {
        val details = UserDetails()
        val box = document.select("div#Main div.box")
        //基础信息
        box.first()?.let {
            details.avatar = str { it.select("img.avatar").attr("src") }
            details.username = str { it.select("h1").text() }
            details.online = it.select("strong.online").hasText()
            val span = it.select("td").last()?.select("span.gray")
            if (span != null) {
                details.no = str { RegexConstant.MEMBER_NO.find(span.first()?.text() ?: "")?.value }
                details.registerAt = str {
                    DateTimeUtils.format(RegexConstant.REGISTER_AT.find(span.first()?.text() ?: "")?.value)
                }
                details.ranking = str { span.select("a[href=/top/dau]").text() }
            }
        }
        //最近发布
        if (box.size > 1) {
            val trArray = box[1].select("div.cell.item tr")
            if (trArray.isNotEmpty()) {
                trArray.forEach { tr ->
                    val topic = UserRecentlyTopic()
                    topic.sid = str {
                        RegexConstant.TOPIC_ID.find(tr.select("span.item_title a.topic-link").attr("href"))?.value
                    }
                    topic.title = str { tr.select("span.item_title a.topic-link").text() }
                    val a = tr.select("span.topic_info a.node")
                    topic.node = TabPair(
                        key = str { RegexConstant.NODE_NAME.find(a.attr("href"))?.value },
                        name = str { a.text() }
                    )
                    topic.time = str { DateTimeUtils.ago(tr.select("span.topic_info span[title]").attr("title")) }
                    topic.replyCount = str { tr.select("a.count_livid").text() }
                    details.topics.add(topic)
                }
            } else {
                val lockImg = box[1].select("div.cell img[src^=/static/img/lock]")
                if (lockImg.isNotEmpty()) {
                    details.topicInvisible = true
                }
            }
        }
        //最近回复
        if (box.size > 2) {
            val topicArray = box[2].select("div.dock_area td")
            val replyArray = box[2].select("div.reply_content")
            if (topicArray.size != replyArray.size) logger.warning("dock_area 和 inner数量不一致")
            else {
                topicArray.forEachIndexed { index, td ->
                    val reply = UserRecentlyReply()
                    reply.author = str { td.select("a[href^=/member/]").text() }
                    val titleEl = td.select("a[href^=/t/]")
                    reply.topic = str { titleEl.text() }
                    reply.sid = str { RegexConstant.TOPIC_ID.find(titleEl.attr("href"))?.value }
                    catch {
                        val nodeEl = td.select("a[href^=/go/]")
                        reply.node =
                            TabPair(RegexConstant.NODE_NAME.find(nodeEl.attr("href"))?.value ?: "", nodeEl.text())
                    }
                    reply.content = replyArray[index].html()
                    reply.time = str { DateTimeUtils.ago(td.select("div.fr span.fade").attr("title")) }
                    details.replys.add(reply)
                }
            }
        }
        return details
    }
}
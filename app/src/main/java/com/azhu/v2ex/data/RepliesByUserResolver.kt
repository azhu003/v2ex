package com.azhu.v2ex.data

import com.azhu.basic.provider.logger
import com.azhu.v2ex.utils.Constant
import org.jsoup.nodes.Document

/**
 * @author: Jerry
 * @date: 2024-10-25 18:50
 * @version: 1.0.0
 */
class RepliesByUserResolver : BaseResolver<Pagination<UserRecentlyReply>>() {
    override fun resolver(document: Document): Pagination<UserRecentlyReply> {
        val pagination = Pagination<UserRecentlyReply>()
        val box = document.select("div#Main > div.box")
        //最近回复
        val topicArray = box.select("div.dock_area td")
        val replyArray = box.select("div.reply_content")
        if (topicArray.size != replyArray.size) logger.w("dock_area 和 inner数量不一致")
        else {
            topicArray.forEachIndexed { index, td ->
                val reply = UserRecentlyReply()
                reply.author = td.select("a[href^=/member/]").text()
                val titleEl = td.select("a[href^=/t/]")
                reply.topic = titleEl.text()
                reply.sid = str { Constant.TOPIC_ID.find(titleEl.attr("href"))?.value }
                catch {
                    val nodeEl = td.select("a[href^=/go/]")
                    reply.node =
                        TabPair(Constant.NODE_NAME.find(nodeEl.attr("href"))?.value ?: "", nodeEl.text())
                }
                reply.content = replyArray[index].html()
                reply.time = td.select("div.fr span.fade").text()
                pagination.data.add(reply)
            }
        }

        //分页数据
        val pages = document.select("div.ps_container").first()?.select("a[href]")
        pagination.page = pages?.select("a.page_current")?.text()?.toIntOrNull() ?: 1
        pagination.total = pages?.last()?.text()?.toIntOrNull() ?: 1
        return pagination
    }
}
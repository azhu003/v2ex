package com.azhu.v2ex.data

import com.azhu.v2ex.utils.Constant
import com.azhu.v2ex.utils.DateTimeUtils
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import org.jsoup.nodes.Document

/**
 * 主题详情解析器
 * @author: Jerry
 * @date: 2024-10-17 00:32
 * @version: 1.0.0
 */
class TopicDetailsResolver(private val resolverType: TopicDetailsResolverType, private val tid: String) :
    BaseResolver<TopicDetails>() {

    override fun resolver(document: Document): TopicDetails {
        val details = TopicDetails(tid = tid)
        if (resolverType == TopicDetailsResolverType.ALL || resolverType == TopicDetailsResolverType.ONLY_TOPIC_BODY) {
            extractTopicDetails(document, details)
        }
        if (resolverType == TopicDetailsResolverType.ALL || resolverType == TopicDetailsResolverType.ONLY_REPLY) {
            extractReplays(document, details)
        }
        return details
    }

    private fun extractTopicDetails(document: Document, details: TopicDetails) {
        val header = document.select("div.header")
        details.title = str { header.select("h1").text() }
        details.author = str { header.select("small.gray a[href^=/member/]").text() }
        details.avatar = str { header.select("img.avatar").attr("src") }
        details.clicks = str { Constant.CLICKS.find(header.select("small.gray").text() ?: "")?.value }

        details.time = str {
            val original = header.select("small.gray span[title]").attr("title")
            return@str DateTimeUtils.format(original)
        }
        val divBox = document.select("div#Main div.box")
        divBox.first()?.let { div ->
            val context = div.select("div.cell div.topic_content")
            details.isMarkdown = context.select("> div.markdown_body").isNotEmpty()
            details.content = str {
                val content = context.html()
                return@str if (details.isMarkdown) {
                    FlexmarkHtmlConverter.builder().build().convert(content)
                } else {
                    content
                }
            }
            document.select("div.subtle").forEach {
                val subtitle = TopicDetailsSubtitle()
                subtitle.time = str { DateTimeUtils.ago(it.select("span[title]").attr("title")) }
                subtitle.content = str { it.select("div.topic_content").html() }
                details.subtitles.add(subtitle)
            }
        }
        //div.topic_buttons 未登录时没有这个div
        val buttons = document.select("div.topic_buttons")
        details.isCollected = buttons.select("> a[href^=/unfavorite/topic/]").isNotEmpty()
        val stats = buttons.select("div.topic_stats").text()
        details.collections = Constant.COLLECTIONS.find(stats)?.value
        details.thanks = Constant.THANKS.find(stats)?.value ?: ""
        details.once = Constant.ONCE.find(buttons.select("> a[href*=favorite/topic/]").attr("href"))?.value
        details.isThanked = buttons.select("span.topic_thanked").isNotEmpty()

        //回复数
        details.replyCount = str {
            val cells = document.select("div.cell[id^=r_]")
            val text = cells.parents().first()?.select("span.gray")?.text() ?: ""
            val count = Constant.REPLY_NUMBERS.find(text)?.value
            count ?: "0"
        }
    }

    private fun extractReplays(document: Document, details: TopicDetails) {
        val pagination = Pagination<TopicReplyItem>()
        //回复列表
        val cells = document.select("div.cell[id^=r_]")
        for (cell in cells) {
            catch {
                val reply = TopicReplyItem()
                reply.id = str { Constant.REPLY_ID.find(cell.attr("id"))?.value }
                val tr = cell.select("tr")
                reply.avatar = str { tr.select("img.avatar").attr("src") }
                reply.username = str { tr.select("strong a[href^=/member/]").text() }
                reply.time = str { DateTimeUtils.ago(tr.select("span.ago[title]").attr("title")) }
                reply.thanks = str { tr.select("span.small.fade").text().trim() }
                reply.isThanked = tr.select("div.thank_area.thanked").isNotEmpty()
                reply.no = str { tr.select("div.fr span.no").text() }
                val badges = tr.select("div.badges > div.badge")
                badges.forEach { reply.badges.add(it.text()) }
                reply.content = str { tr.select("div.reply_content").html() }
                pagination.data.add(reply)
            }
        }
        //分页数据
        val pages = document.select("div.ps_container").first()?.select("a[href^=?p]")
        pagination.page = pages?.select("a.page_current")?.text()?.toIntOrNull() ?: 1
        pagination.total = pages?.last()?.text()?.toIntOrNull() ?: 1
        details.replies = pagination
    }
}
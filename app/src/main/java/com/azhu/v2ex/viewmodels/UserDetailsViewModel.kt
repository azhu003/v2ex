package com.azhu.v2ex.viewmodels

import android.text.TextUtils
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.NodeInfo
import com.azhu.v2ex.data.UserDetails
import com.azhu.v2ex.data.UserRecentlyReply
import com.azhu.v2ex.data.UserRecentlySubject
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.utils.DateTimeUtils
import com.azhu.v2ex.utils.RegexConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import org.jsoup.nodes.Document

/**
 * @author: azhu
 * @date: 2024-10-11 15:54
 * @version: 1.0.0
 */
class UserDetailsViewModel : BaseViewModel() {

    val state = mutableStateOf(UserDetails())

    fun fetchData() {
        if (TextUtils.isEmpty(state.value.username)) throw IllegalArgumentException("username is empty")

//        getHtmlFromAssets("member.html")
//            .map { Result.success(getDocument(it.getOrThrow())) }
//            .flowOn(Dispatchers.IO)
//            .error { logger.error(it?.message ?: "error message is null") }
//            .success { setUserDetails(it) }
//            .launchIn(viewModelScope)

        http.fetch { http.service.getMemberDetails(state.value.username) }
            .smap { Result.success(getDocument(it.byteStream())) }
            .flowOn(Dispatchers.IO)
            .error { logger.error(it?.message ?: "error message is null") }
            .success { setUserDetails(it) }
            .launchIn(viewModelScope)
    }

    private fun setUserDetails(document: Document) {
        val details = UserDetails()
        val box = document.select("div#Main div.box")
        //基础信息
        box.first()?.let {
            details.avatar = str { it.select("img.avatar").attr("src") }
            details.username = str { it.select("h1").text() }
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
                    val subject = UserRecentlySubject()
                    subject.sid = str {
                        RegexConstant.TOPIC_ID.find(tr.select("span.item_title a.topic-link").attr("href"))?.value
                    }
                    subject.title = str { tr.select("span.item_title a.topic-link").text() }
                    val a = tr.select("span.topic_info a.node")
                    subject.node = NodeInfo(
                        key = str { RegexConstant.NODE_NAME.find(a.attr("href"))?.value },
                        name = str { a.text() }
                    )
                    subject.time = str { DateTimeUtils.ago(tr.select("span.topic_info span[title]").attr("title")) }
                    subject.replyCount = str { tr.select("a.count_livid").text() }
                    details.subjects.add(subject)
                }
            } else {
                val lockImg = box[1].select("div.cell img[src^=/static/img/lock]")
                if (lockImg.isNotEmpty()) {
                    details.subjectHidden = true
                }
            }
        }
        //最近回复
        if (box.size > 2) {
            val subjectArray = box[2].select("div.dock_area td")
            val replyArray = box[2].select("div.reply_content")
            if (subjectArray.size != replyArray.size) logger.warning("dock_area 和 inner数量不一致")
            else {
                subjectArray.forEachIndexed { index, td ->
                    val reply = UserRecentlyReply()
                    reply.author = str { td.select("a[href^=/member/]").text() }
                    val titleEl = td.select("a[href^=/t/]")
                    reply.subject = str { titleEl.text() }
                    reply.sid = str { RegexConstant.TOPIC_ID.find(titleEl.attr("href"))?.value }
                    catch {
                        val nodeEl = td.select("a[href^=/go/]")
                        reply.node =
                            NodeInfo(RegexConstant.NODE_NAME.find(nodeEl.attr("href"))?.value ?: "", nodeEl.text())
                    }
                    reply.content = replyArray[index].html()
                    reply.time = str { DateTimeUtils.ago(td.select("div.fr span.fade").attr("title")) }
                    details.replys.add(reply)
                }
            }
        }
        state.value = details
    }
}
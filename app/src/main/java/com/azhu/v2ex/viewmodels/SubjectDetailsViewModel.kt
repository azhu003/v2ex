package com.azhu.v2ex.viewmodels

import android.content.Context
import android.text.TextUtils
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.SubjectDetails
import com.azhu.v2ex.data.SubjectReplyItem
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.ui.activity.UserDetailsActivity
import com.azhu.v2ex.utils.DateTimeUtils
import com.azhu.v2ex.utils.RegexConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import org.jsoup.nodes.Document

/**
 * @description:
 * @author: azhu
 * @date: 2024-10-02 00:33
 * @version: 1.0.0
 */
class SubjectDetailsViewModel : BaseViewModel() {

    val state = mutableStateOf(SubjectDetails())

    fun onViewUserClick(context: Context, item: SubjectReplyItem) {
        UserDetailsActivity.start(context, item.username)
    }

    fun fetchSubjectDetails() {
        val details = state.value
        if (TextUtils.isEmpty(details.sid)) throw NullPointerException("sid is null")
//        getHtmlFromAssets("subject.html")
//            .map { Result.success(getDocument(it.getOrThrow())) }
//            .flowOn(Dispatchers.IO)
//            .error { logger.error(it?.message ?: "error message is null") }.success { setSubjectDetails(it) }
//            .launchIn(viewModelScope)
        http.fetch { http.service.getSubjectDetails(details.sid!!) }
            .smap { Result.success(getDocument(it.byteStream())) }
            .flowOn(Dispatchers.IO)
            .error { logger.error(it?.message ?: "error message is null") }
            .success { setSubjectDetails(it) }
            .launchIn(viewModelScope)
    }

    private fun setSubjectDetails(document: Document) {
        val details = SubjectDetails()
        val header = document.select("div.header")
        details.title = str { header.select("h1").text() }
        details.author = str { header.select("small.gray a[href^=/member/]").text() }
        details.clicks = str {
            RegexConstant.CLICKS.find(header.select("small.gray").last()?.text() ?: "")?.value
        }

        details.time = str {
            val original = header.select("small.gray span[title]").attr("title")
            return@str DateTimeUtils.format(original)
        }
        details.content = str { document.select("div.topic_content").html() }
        // div.topic_buttons 未渲染？
//        catch {
//            val stats = document.select("div.topic_stats").text()
//            details.clicks = RegexConstant.CLICKS.find(stats)?.value ?: ""
//            details.collections = RegexConstant.COLLECTIONS.find(stats)?.value ?: ""
//            details.useful = RegexConstant.USEFUL.find(stats)?.value ?: ""
//        }

        //回复列表
        val cells = document.select("div.cell[id^=r_]")
        //回复数
        details.replyCount = str {
            val text = cells.parents().first()?.select("span.gray")?.text() ?: ""
            RegexConstant.REPLY_NUMBERS.find(text)?.value
        }
        for (cell in cells) {
            catch {
                val reply = SubjectReplyItem()
                reply.id = str { RegexConstant.REPLY_ID.find(cell.attr("id"))?.value }

                val tr = cell.select("tr")
                reply.avatar = str { tr.select("img.avatar").attr("src") }
                reply.username = str { tr.select("strong a[href^=/member/]").text() }
                reply.time = str { DateTimeUtils.ago(tr.select("span.ago[title]").attr("title")) }
                reply.isAuthor = TextUtils.equals(reply.username, details.author)
                reply.content = str { tr.select("div.reply_content").html() }
                details.reply.add(reply)
            }
        }
        state.value = details
    }
}
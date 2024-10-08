package com.azhu.v2ex.viewmodels

import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.RegexConstant
import com.azhu.v2ex.data.SubjectDetailsState
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * @description:
 * @author: azhu
 * @date: 2024-10-02 00:33
 * @version: 1.0.0
 */
class SubjectDetailsViewModel : BaseViewModel() {

    val details = SubjectDetailsState()

    fun fetchSubjectDetails() {
//        getHtmlFromAssets("subject.html")
        if (TextUtils.isEmpty(details.sid)) throw NullPointerException("sid is null")
        http.fetch { http.service.getSubjectDetails(details.sid!!) }
            .map { Result.success(getDocument(it.getOrThrow().byteStream())) }
            .flowOn(Dispatchers.IO)
            .error { logger.error(it?.message ?: "error message is null") }
            .success { setSubjectDetails(it) }
            .launchIn(viewModelScope)
    }

    private fun setSubjectDetails(document: Document) {
        val header = document.select("div.header")
        details.title.value = str { header.select("h1").text() }
        details.author.value = str { header.select("small.gray a[href^=/member/]").text() }

        details.time.value = str {
            val original = header.select("small.gray span[title]").attr("title")
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
            val time = format.parse(original)?.let { format.format(it) }
            return@str time ?: ""
        }
        details.content.value = str { document.select("div.topic_content").html() }
        catch {
            val stats = document.select("div.topic_stats").text()
            details.clicks.value = RegexConstant.CLICKS.find(stats)?.value ?: ""
            details.collections.value = RegexConstant.COLLECTIONS.find(stats)?.value ?: ""
            details.useful.value = RegexConstant.USEFUL.find(stats)?.value ?: ""
        }
    }
}
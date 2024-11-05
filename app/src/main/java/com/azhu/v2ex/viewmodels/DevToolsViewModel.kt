package com.azhu.v2ex.viewmodels

import android.net.Uri
import androidx.activity.result.ActivityResultCallback
import androidx.lifecycle.viewModelScope
import com.azhu.basic.AppManager
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.BaseResolver
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.ui.component.LoadingDialogState
import com.azhu.v2ex.ui.component.MessageDialogState
import com.azhu.v2ex.ui.component.ReplayDialogState
import com.azhu.v2ex.ui.component.ReplaySheetState
import com.azhu.v2ex.ui.component.getEmotionUrl
import com.azhu.v2ex.utils.V2exUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.jsoup.nodes.Document

/**
 * @author: Jerry
 * @date: 2024-11-02 00:08
 * @version: 1.0.0
 */
class DevToolsViewModel : BaseViewModel() {

    val messageDialog = MessageDialogState()
    val loadingDialog = LoadingDialogState()
    val replyDialog = ReplayDialogState(
        onSubmit = ::onSubmit
    )
    val reply = ReplaySheetState(
        onInsertEmoji = ::onInsertEmoji,
        onInsertLink = ::onInsertLink,
        onInsertImage = ::onInsertImage,
        onSubmit = ::onSubmit
    )
    val registerForActivityResult: ActivityResultCallback<Uri?> = ActivityResultCallback { uri ->
        logger.i("ActivityResultCallback -> selected file uri = $uri")
        if (uri != null) {
            val context = AppManager.getCurrentActivity()
            if (context != null) {
                val path = V2exUtils.getRealPathFromURI(context, uri)
                logger.i("ActivityResultCallback context -> ${context.javaClass.name} path = $path")
            }
        }
    }

    private fun onInsertEmoji(emoji: String) {
        reply.insertToContent("${getEmotionUrl(emoji)} ")
    }

    private fun onInsertImage() {

    }

    private fun onInsertLink(text: String, url: String) {
        reply.insertToContent("![$text]($url)")
    }

    fun onSubmit() {
        val parser = Parser.builder().build()
        val document = parser.parse(reply.content.text)
        val content = HtmlRenderer.builder().build().render(document)
        logger.i("content = $content")
    }

    fun readEmoji() {
        http.flows(
            doRequest = {
                val input = DataRepository.INSTANCE.getHtmlFromAssets("emoji.html")
                return@flows EmojiResolver().resolver(input)
            })
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error {
                logger.e("emoji error => $it")
            }
            .success {
                logger.i("emoji => $it")
            }
            .launchIn(viewModelScope)
    }

    private class EmojiResolver : BaseResolver<List<String>>() {
        override fun resolver(document: Document): List<String> {
            val data = mutableListOf<String>()
            val els = document.select("div.thumb-title")
//            els.forEach { data.add("\"${it.attr("id")}\"") }
            els.forEachIndexed { index, el -> data.add("Pair(\"emotion_${index + 1}\", \"${el.attr("id")}\")") }
            return data
        }
    }
}
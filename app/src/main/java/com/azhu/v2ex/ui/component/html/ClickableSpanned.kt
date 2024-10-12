package com.azhu.v2ex.ui.component.html

import android.text.Spannable
import android.text.style.ImageSpan
import android.text.style.URLSpan
import android.widget.TextView
import androidx.core.text.getSpans
import com.azhu.v2ex.utils.V2exUtils

/**
 * @author: azhu
 * @date: 2024-10-08 14:11
 * @version: 1.0.0
 */
object ClickableSpanned {

    fun makeLinksClickable(textView: TextView) {
        val spannable = textView.text as Spannable
        val spans = spannable.getSpans<Any>(0, spannable.length)
        for (span in spans) {
            if (span is ImageSpan || span is URLSpan) {
                val start = spannable.getSpanStart(span)
                val end = spannable.getSpanEnd(span)
                val flags = spannable.getSpanFlags(span)

                if (span is ImageSpan) {
                    // 替换为自定义的ClickableSpan
                    if (!span.source.isNullOrBlank()) {
                        spannable.setSpan(ImageClickableSpan(span.source!!), start, end, flags)
                    }
                } else if (span is URLSpan) {
                    var url = span.url
                    if (V2exUtils.isIntraSiteLink(span.url)) {
                        url = V2exUtils.getRelativeURL(span.url)
                    }
                    // 移除默认的URLSpan
                    spannable.removeSpan(span)
                    if (V2exUtils.isMemberUrl(url) && start > 0) {
                        var offsetStart = start
                        if ('@' == spannable[start - 1]) offsetStart -= 1
                        spannable.setSpan(URLClickableSpan(url), offsetStart, end, flags)
                    } else {
                        spannable.setSpan(URLClickableSpan(url), start, end, flags)
                    }
                }
            }
        }
    }

}
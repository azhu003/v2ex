package com.azhu.v2ex.ui.component.html

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import com.azhu.basic.provider.logger


/**
 * @author: azhu
 * @date: 2024-10-08 14:11
 * @version: 1.0.0
 */
object ClickableSpanned {

    private fun setLinkClickable(clickableHtmlBuilder: SpannableStringBuilder, urlSpan: URLSpan) {
        val start = clickableHtmlBuilder.getSpanStart(urlSpan)
        val end = clickableHtmlBuilder.getSpanEnd(urlSpan)
        val flags = clickableHtmlBuilder.getSpanFlags(urlSpan)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                //Do something with URL here.
                // 如获取url地址，跳转到自己指定的页面
                logger.info("点击了链接 -> ${urlSpan.url}")
            }
        }
        clickableHtmlBuilder.setSpan(clickableSpan, start, end, flags)
    }

    fun getClickableHtml(spannedHtml: Spanned): CharSequence {
        val clickableHtmlBuilder = SpannableStringBuilder(spannedHtml)
        val urls = clickableHtmlBuilder.getSpans(0, spannedHtml.length, URLSpan::class.java)
        for (span in urls) {
            setLinkClickable(clickableHtmlBuilder, span)
        }
        return clickableHtmlBuilder
    }

}
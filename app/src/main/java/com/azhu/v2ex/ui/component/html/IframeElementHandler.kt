package com.azhu.v2ex.ui.component.html

import android.text.Spannable
import android.text.SpannableString
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import com.azhu.basic.AppManager
import com.azhu.v2ex.R
import org.xml.sax.XMLReader

/**
 * @author: Jerry
 * @date: 2024-10-27 14:51
 * @version: 1.0.0
 */
class IframeElementHandler : ElementTagHandler {

    private var src: String? = null

    override fun opening(reader: XMLReader) {
        src = getAttribute(reader, "src")
    }

    override fun closing(): Spannable? {
        if (src == null) return null

        val spannable = SpannableString("[iframe]")

        AppManager.getCurrentActivity()?.let {
            val image = ImageSpan(it, R.drawable.video, DynamicDrawableSpan.ALIGN_BASELINE)
            spannable.setSpan(image, 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        spannable.setSpan(URLClickableSpan(src!!), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }
}
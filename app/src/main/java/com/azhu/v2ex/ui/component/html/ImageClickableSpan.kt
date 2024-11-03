package com.azhu.v2ex.ui.component.html

import android.text.style.ClickableSpan
import android.view.View
import com.azhu.basic.provider.logger

/**
 * @author: azhu
 * @date: 2024-10-10 19:18
 * @version: 1.0.0
 */
class ImageClickableSpan(private val source: String) : ClickableSpan() {

    override fun onClick(weight: View) {
        logger.i("click image -> source: $source")
    }
}
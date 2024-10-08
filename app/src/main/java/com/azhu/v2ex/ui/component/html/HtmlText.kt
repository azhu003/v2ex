package com.azhu.v2ex.ui.component.html

import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.azhu.basic.provider.AppThemeProvider
import com.azhu.v2ex.ui.theme.onContainerPrimaryDark
import com.azhu.v2ex.ui.theme.onContainerPrimaryLight

/**
 * @author: azhu
 * @date: 2024-10-07 12:50
 * @version: 1.0.0
 */
@Composable
fun HtmlText(html: String, modifier: Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val view = TextView(context)
            view.linksClickable = true
            view.movementMethod = LinkMovementMethod.getInstance()
            Linkify.addLinks(view, Linkify.WEB_URLS)

            val color = if (AppThemeProvider.isDark()) {
                Color.argb(
                    onContainerPrimaryDark.alpha,
                    onContainerPrimaryDark.red,
                    onContainerPrimaryDark.green,
                    onContainerPrimaryDark.blue
                )
            } else {
                Color.argb(
                    onContainerPrimaryLight.alpha,
                    onContainerPrimaryLight.red,
                    onContainerPrimaryLight.green,
                    onContainerPrimaryLight.blue
                )
            }
            view.setTextColor(color)
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            view.lineHeight = 80f.dp.value.toInt()
            view
        },
        update = {
            val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT, TextImageGetter(it), null)
            it.text = ClickableSpanned.getClickableHtml(spanned)
        },
    )
}
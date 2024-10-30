package com.azhu.v2ex.ui.component.html

import android.content.Context
import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.azhu.basic.provider.AppThemeProvider
import com.azhu.v2ex.http.Retrofits
import com.azhu.v2ex.ui.component.markdown.GrammarLocatorDef
import com.azhu.v2ex.ui.theme.backgroundDark
import com.azhu.v2ex.ui.theme.backgroundLight
import com.azhu.v2ex.ui.theme.onContainerPrimaryDark
import com.azhu.v2ex.ui.theme.onContainerPrimaryLight
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TableAwareMovementMethod
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.movement.MovementMethodPlugin
import io.noties.markwon.syntax.Prism4jThemeDarkula
import io.noties.markwon.syntax.Prism4jThemeDefault
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j

/**
 * @author: azhu
 * @date: 2024-10-07 12:50
 * @version: 1.0.0
 */
@Composable
fun HtmlText(modifier: Modifier, html: String, isMarkdown: Boolean = false, fontSize: Float = 16f) {
    val width = remember { mutableFloatStateOf(0f) }
    AndroidView(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                if (width.floatValue == 0f) {
                    width.floatValue = coordinates.boundsInParent().width
                }
            },
        factory = { context ->
            val textview = TextView(context)
            Linkify.addLinks(textview, Linkify.WEB_URLS)
            Linkify.addLinks(textview, Linkify.EMAIL_ADDRESSES)
            Linkify.addLinks(textview, Linkify.PHONE_NUMBERS)
            textview.movementMethod = LinkMovementMethod.getInstance()
            textview.setTextColor(getTextColor())
            textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
//            view.lineHeight = 80f.dp.value.toInt()
//            logger.info("factory -> 构造TextView ")
//            val helper = SelectableTextHelper.Builder(textview)
//                .setSelectedColor(textview.resources.getColor(R.color.purple_200))
//                .setCursorHandleSizeInDp(20f)
//                .setCursorHandleColor(textview.resources.getColor(R.color.purple_500))
//                .build()
            textview
        },
        update = {
            if (isMarkdown) {
                val markdown = getMarkdown(it.context)
                markdown.setMarkdown(it, html)
            } else {
                it.text = HtmlCompat.fromHtml(
                    "$html ", //末尾加空字符防止内容仅一张图片时无法正常显示
                    HtmlCompat.FROM_HTML_MODE_COMPACT,
                    TextImageGetter(it, width.floatValue, fontSize.dp),
                    ElementTagHandler.Builder()
                        .addHandler("iframe", IframeElementHandler())
                        .build()
                )
            }
            ClickableSpanned.makeLinksClickable(it)
        }
    )
}

private fun getMarkdown(context: Context): Markwon {
    return Markwon.builder(context)
        .usePlugin(CoilImagesPlugin.create(context, Retrofits.imageLoader))
        .usePlugin(LinkifyPlugin.create())
        .usePlugin(TablePlugin.create(context))
        .usePlugin(MovementMethodPlugin.create(TableAwareMovementMethod.create()))
        .usePlugin(
            SyntaxHighlightPlugin.create(
                Prism4j(GrammarLocatorDef()),
                if (AppThemeProvider.isDark()) Prism4jThemeDarkula(backgroundDark.toArgb())
                else Prism4jThemeDefault(backgroundLight.toArgb())
            )
        )
        .build()
}

private fun getTextColor(): Int {
    return if (AppThemeProvider.isDark()) {
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
}
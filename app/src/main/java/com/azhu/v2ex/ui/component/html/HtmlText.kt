package com.azhu.v2ex.ui.component.html

import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import br.tiagohm.markdownview.MarkdownView
import br.tiagohm.markdownview.css.styles.Github
import com.azhu.basic.provider.AppThemeProvider
import com.azhu.v2ex.ui.theme.onContainerPrimaryDark
import com.azhu.v2ex.ui.theme.onContainerPrimaryLight

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
            if (isMarkdown) {
                val style = Github()
                style.addRule("body", *arrayOf("font-family: \"Helvetica Neue\", Helvetica, Arial, sans-serif", "font-size: 14px", "line-height: 1.42857143", "color: #FFF", "background-color: #161418", "margin: 0"))

                val markdown = MarkdownView(context)
                markdown.addStyleSheet(style)

                //http://stackoverflow.com/questions/6370690/media-queries-how-to-target-desktop-tablet-and-mobile
//                style.addMedia("screen and (min-width: 320px)")
//                style.addRule("h1", "color: green")
//                style.endMedia()
//                style.addMedia("screen and (min-width: 481px)")
//                style.addRule("h1", "color: red")
//                style.endMedia()
//                style.addMedia("screen and (min-width: 641px)")
//                style.addRule("h1", "color: blue")
//                style.endMedia()
//                style.addMedia("screen and (min-width: 961px)")
//                style.addRule("h1", "color: yellow")
//                style.endMedia()
//                style.addMedia("screen and (min-width: 1025px)")
//                style.addRule("h1", "color: gray")
//                style.endMedia()
//                style.addMedia("screen and (min-width: 1281px)")
//                style.addRule("h1", "color: orange")
//                style.endMedia()
//                style.addRule("a", "color: #42A5F5")
//                style.endMedia()
                markdown
            } else {
                val textview = TextView(context)
                Linkify.addLinks(textview, Linkify.WEB_URLS)
                Linkify.addLinks(textview, Linkify.EMAIL_ADDRESSES)
                Linkify.addLinks(textview, Linkify.PHONE_NUMBERS)

                textview.movementMethod = LinkMovementMethod.getInstance()
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
                textview.setTextColor(color)
                textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
//            view.lineHeight = 80f.dp.value.toInt()
//            logger.info("factory -> 构造TextView ")
//            val helper = SelectableTextHelper.Builder(textview)
//                .setSelectedColor(textview.resources.getColor(R.color.purple_200))
//                .setCursorHandleSizeInDp(20f)
//                .setCursorHandleColor(textview.resources.getColor(R.color.purple_500))
//                .build()
                textview
            }
        },
        update = {
            if (it is MarkdownView) {
                it.loadMarkdown(html)
//                it.loadMarkdownFromAsset("topic.md")
            } else if (it is TextView) {
                val spanned =
                    HtmlCompat.fromHtml(
                        "$html ", //末尾加空字符防止内容仅一张图片时无法正常显示
                        HtmlCompat.FROM_HTML_MODE_COMPACT,
                        TextImageGetter(it, width.floatValue, fontSize.dp),
                        ElementTagHandler.Builder()
                            .addHandler("iframe", IframeElementHandler())
                            .build()
                    )
                it.text = spanned
                ClickableSpanned.makeLinksClickable(it)
            }
        },
        onRelease = {
            if (it is MarkdownView) {
                it.destroy()
            }
        }
    )
}
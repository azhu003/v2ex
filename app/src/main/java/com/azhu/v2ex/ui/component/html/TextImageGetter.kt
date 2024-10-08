package com.azhu.v2ex.ui.component.html

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.text.Html.ImageGetter
import android.widget.TextView
import coil.imageLoader
import coil.request.ImageRequest
import com.azhu.basic.provider.AppThemeProvider
import com.azhu.basic.provider.logger
import com.azhu.v2ex.ui.theme.containerDark
import com.azhu.v2ex.ui.theme.containerLight
import java.lang.ref.WeakReference

/**
 * @author: azhu
 * @date: 2024-10-07 12:57
 * @version: 1.0.0
 */
class TextImageGetter(textView: TextView) : ImageGetter {

    private var mTextViewReference: WeakReference<TextView> = WeakReference(textView)

    override fun getDrawable(url: String?): Drawable {
        val text = mTextViewReference.get() ?: throw NullPointerException("textview is null")
        val level = LevelListDrawable()
//        val empty = ResourcesCompat.getDrawable(text.resources, R.drawable.img_loading, text.context.theme)
        val empty = getPlaceholder()
        level.addLevel(0, 0, empty)
        level.setBounds(0, 0, 500, 500)

        logger.info("text width: ${text.measuredWidth} height: ${text.height}")

        if (url != null) {
            fetchAsyncImage(url, level)
        }
        return level
    }

    private fun fetchAsyncImage(url: String, level: LevelListDrawable) {
        mTextViewReference.get()?.let { text ->
            val request = ImageRequest.Builder(text.context)
                .data(url)
                .target { drawable ->
                    resize(drawable, text.resources.displayMetrics.widthPixels.toFloat())
                    level.addLevel(1, 1, drawable)
                    level.setLevel(1)
                    level.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                    text.invalidate()
                    //避免多图片重叠
                    val t: CharSequence = text.text
                    text.text = t
                }
                .build()
            return@let text.context.imageLoader.enqueue(request)
        }
    }

    private fun resize(drawable: Drawable, sw: Float) {
        var w = drawable.intrinsicWidth
        var h = drawable.intrinsicHeight

        //对图片大小进行等比例放大 此处宽高可自行调整
        if (w < h && h > 0) {
            val scale = (sw / h)
            w = (scale * w).toInt()
            h = (scale * h).toInt()
        } else if (w > h && w > 0) {
            val scale = (sw / w)
            w = (scale * w).toInt()
            h = (scale * h).toInt()
        }
        drawable.setBounds(0, 0, w, h)
    }

    private fun getPlaceholder(): Drawable {
        val drawable = if (AppThemeProvider.isDark()) {
            ColorDrawable(
                Color.argb(
                    containerDark.alpha,
                    containerDark.red,
                    containerDark.green,
                    containerDark.blue
                )
            )
        } else {
            ColorDrawable(
                Color.argb(
                    containerLight.alpha,
                    containerLight.red,
                    containerLight.green,
                    containerLight.blue
                )
            )
        }
        return drawable
    }
}
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
import com.azhu.v2ex.ui.theme.onContainerSecondaryDark
import com.azhu.v2ex.ui.theme.onContainerSecondaryLight
import java.lang.ref.WeakReference

/**
 * @author: azhu
 * @date: 2024-10-07 12:57
 * @version: 1.0.0
 */
class TextImageGetter(textView: TextView) : ImageGetter {

    private var mTextViewReference: WeakReference<TextView> = WeakReference(textView)

    override fun getDrawable(url: String?): Drawable {
        val level = LevelListDrawable()
        val empty = getPlaceholder()
        level.addLevel(0, 0, empty)
        level.setBounds(0, 0, 100, 100)

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
                    val tWith = text.right - text.left
                    resize(drawable, tWith.toFloat())
                    //level=0 将占位图的大小设置为实际图片一致
                    level.setBounds(0, 0, drawable.bounds.right, drawable.bounds.bottom)
                    level.addLevel(1, 1, drawable)
                    level.setLevel(1)
                    level.setBounds(0, 0, drawable.bounds.right, drawable.bounds.bottom)
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
                    onContainerSecondaryDark.alpha,
                    onContainerSecondaryDark.red,
                    onContainerSecondaryDark.green,
                    onContainerSecondaryDark.blue
                )
            )
        } else {
            ColorDrawable(
                Color.argb(
                    onContainerSecondaryLight.alpha,
                    onContainerSecondaryLight.red,
                    onContainerSecondaryLight.green,
                    onContainerSecondaryLight.blue
                )
            )
        }
        drawable.setBounds(0, 0, 200, 200)
        return drawable
    }
}
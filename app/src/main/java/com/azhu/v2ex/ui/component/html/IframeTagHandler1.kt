package com.azhu.v2ex.ui.component.html

import android.text.Editable
import android.text.Html.TagHandler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import com.azhu.basic.AppManager
import com.azhu.basic.provider.logger
import com.azhu.v2ex.R
import org.xml.sax.XMLReader
import java.util.Stack


/**
 * @author: Jerry
 * @date: 2024-10-24 18:55
 * @version: 1.0.0
 */
class IframeTagHandler1 : TagHandler {

    /**
     * html 标签的开始下标
     */
    private var startIndex: Stack<Int>? = null

    /**
     * html的标签的属性值 value，如:<size value='16'></size>
     * 注：value的值不能带有单位,默认就是sp
     */
    private var propertyValue: Stack<String>? = null

    override fun handleTag(opening: Boolean, tag: String?, output: Editable?, reader: XMLReader?) {
        logger.info("handleTag -> $tag")
        if ("iframe".equals(tag, true)) {
            if (output != null && reader != null) {
                iframe(opening, output, reader)
            }
        } else if ("a".equals(tag, true)) {
            if (output != null && reader != null) {
                iframe(opening, output, reader)
            }
        }
    }

    private fun iframe(opening: Boolean, output: Editable, reader: XMLReader) {
        if (opening) {
            handleStartIframe(output, reader)
        } else {
            handleEndIframe(output)
        }
    }

    private fun handleStartIframe(output: Editable, reader: XMLReader) {
        val len = output.length
        if (startIndex == null) startIndex = Stack()
        startIndex?.push(len)

        if (propertyValue == null) propertyValue = Stack()

        // 获取标签属性
        val attributes = getAttribute(reader, "src")
        propertyValue?.push(attributes)
    }

    private fun handleEndIframe(output: Editable) {
        if (propertyValue.isNullOrEmpty() && startIndex.isNullOrEmpty()) return

        try {
            val start: Int = startIndex!!.pop()
            val url: String = propertyValue!!.pop()

            val spannable = SpannableString("iframe")

            AppManager.getCurrentActivity()?.let {
                val image = ImageSpan(it, R.drawable.video, DynamicDrawableSpan.ALIGN_BASELINE)
                spannable.setSpan(image, 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            spannable.setSpan(URLClickableSpan(url), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            output.insert(start, spannable, 0, spannable.length)
        } catch (e: Exception) {
            logger.error("handleEndIframe error: $e")
            e.printStackTrace()
        }
    }

    /**
     * 利用反射获取html标签的属性值
     *
     * @param xmlReader XmlReader
     * @param attr 属性名
     * @return
     */
    private fun getAttribute(xmlReader: XMLReader, attr: String): String? {
        try {
            val elementField = xmlReader.javaClass.getDeclaredField("theNewElement")
            elementField.isAccessible = true
            val element = elementField[xmlReader]
            val attrsField = element.javaClass.getDeclaredField("theAtts")
            attrsField.isAccessible = true
            val attrs = attrsField[element]
            val dataField = attrs.javaClass.getDeclaredField("data")
            dataField.isAccessible = true
            val data = dataField[attrs] as Array<*>
            val lengthField = attrs.javaClass.getDeclaredField("length")
            lengthField.isAccessible = true
            val len = lengthField[attrs] as Int

            for (i in 0 until len) {
                // 这边的property换成你自己的属性名就可以了
                if (attr == data[i * 5 + 1]) {
                    return data[i * 5 + 4] as String
                }
            }
        } catch (e: Exception) {
            logger.error("decode attributes error: $e")
        }
        return null
    }
}
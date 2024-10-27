package com.azhu.v2ex.ui.component.html

import android.text.Editable
import android.text.Html.TagHandler
import org.xml.sax.XMLReader
import java.util.Stack

/**
 * @author: Jerry
 * @date: 2024-10-27 14:17
 * @version: 1.0.0
 */
class ElementTagHandlerImpl(private val handlers: Map<String, ElementTagHandler>) : TagHandler {

    /**
     * html 标签的开始下标
     */
    private var startIndex: Stack<Int>? = null

    /**
     * html的标签的属性值 value，如:<size value='16'></size>
     * 注：value的值不能带有单位,默认就是sp
     */
    private var attrValue: Stack<String>? = null

    override fun handleTag(opening: Boolean, tag: String?, output: Editable?, reader: XMLReader?) {
        val handler = handlers[tag] ?: return
        if (output == null || reader == null) return
        if (opening) {
            if (startIndex == null) startIndex = Stack()
            if (attrValue == null) attrValue = Stack()

            val len = output.length
            startIndex!!.push(len)

            handler.opening(reader)
        } else {
            if (startIndex == null) return
            val start: Int = startIndex!!.pop()
            val spanned = handler.closing()
            if (spanned != null) {
                output.insert(start, spanned, 0, spanned.length)
            }
        }
    }
}
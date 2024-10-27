package com.azhu.v2ex.ui.component.html

import android.text.Html.TagHandler
import android.text.Spannable
import com.azhu.basic.provider.logger
import org.xml.sax.XMLReader

/**
 * @author: Jerry
 * @date: 2024-10-27 14:26
 * @version: 1.0.0
 */
interface ElementTagHandler {

    fun opening(reader: XMLReader)

    fun closing(): Spannable?

    /**
     * 利用反射获取html标签的属性值
     *
     * @param xmlReader XmlReader
     * @param attr 属性名
     * @return
     */
    fun getAttribute(xmlReader: XMLReader, attr: String): String? {
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

    class Builder {

        private val handlers = mutableMapOf<String, ElementTagHandler>()

        fun addHandler(tag: String, handler: ElementTagHandler): Builder {
            handlers[tag] = handler
            return this
        }

        fun build(): TagHandler {
            return ElementTagHandlerImpl(handlers)
        }
    }
}
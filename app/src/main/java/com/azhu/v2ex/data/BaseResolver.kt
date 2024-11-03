package com.azhu.v2ex.data

import com.azhu.basic.provider.logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.InputStream

/**
 * @author: Jerry
 * @date: 2024-10-16 03:23
 * @version: 1.0.0
 */
abstract class BaseResolver<T> : DocumentResolver<T> {

    override fun resolver(stream: InputStream): T {
        stream.use {
            val document = Jsoup.parse(it, "UTF-8", "https://www.v2ex.com/")
            catch {
                document.select("[data-cfemail]").forEach { element ->
                    val data = element.attr("data-cfemail")
                    val email = getUnprotectMail(data)
                    logger.i("查询到保护邮箱 -> data=$data email=$email element=$element")
                    if (element.tagName().lowercase() != "a") {
                        element.tagName("a")
                    }
                    if (email != null) {
                        element.attr("href", "mailto:$email").text(email)
                    }
                }
            }
            return resolver(document)
        }
    }

    abstract fun resolver(document: Document): T

    protected inline fun str(block: () -> String?): String {
        return try {
            block.invoke() ?: ""
        } catch (e: Exception) {
            logger.i("${this::class.simpleName} -> $e")
            ""
        }
    }

    protected inline fun catch(block: () -> Unit) {
        try {
            block.invoke()
        } catch (e: Exception) {
            logger.i("${this::class.simpleName} -> $e")
        }
    }

    /**
     * 对邮箱地址进行解密
     */
    private fun getUnprotectMail(data: String?): String? {
        if (!data.isNullOrEmpty()) {
            val r = data.substring(0, 2).toInt(16)
            var i: Int
            val email = StringBuilder()
            for (n in 2 until data.length step 2) {
                i = data.substring(n, n + 2).toInt(16).xor(r)
                email.append(i.toChar())
            }
            if (email.isNotEmpty()) return email.toString()
        }
        return null
    }
}
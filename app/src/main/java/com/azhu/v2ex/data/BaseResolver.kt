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
        val document = Jsoup.parse(stream, "UTF-8", "https://www.v2ex.com/")
        return resolver(document)
    }

    abstract fun resolver(document: Document): T

    protected inline fun str(block: () -> String?): String {
        return try {
            block.invoke() ?: ""
        } catch (e: Exception) {
            logger.info("${this::class.simpleName} -> $e")
            ""
        }
    }

    protected inline fun catch(block: () -> Unit) {
        try {
            block.invoke()
        } catch (e: Exception) {
            logger.info("${this::class.simpleName} -> $e")
        }
    }

}
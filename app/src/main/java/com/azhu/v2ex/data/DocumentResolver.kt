package com.azhu.v2ex.data

import java.io.InputStream

/**
 * @author: Jerry
 * @date: 2024-10-16 03:22
 * @version: 1.0.0
 */
interface DocumentResolver<T> {

    fun resolver(stream: InputStream): T
}
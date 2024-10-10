package com.azhu.v2ex.utils

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

/**
 * @author: azhu
 * @date: 2024-10-10 01:33
 * @version: 1.0.0
 */
class RegexConstantTest {

    @Test
    fun getCLICKS() {
        var value = RegexConstant.CLICKS.find("1天前 · 686 次点击  ")?.value
        assertSame(value, "686")
        value = RegexConstant.CLICKS.find("2023-07-26 09:36:59 +08:00 · 686 次点击  ")?.value
        assertSame(value, "686")
    }
}
package com.azhu.v2ex.utils

import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * @author: azhu
 * @date: 2024-10-09 13:16
 * @version: 1.0.0
 */
object DateTimeUtils {

    private val DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX")
    private val DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")
    private val SHORT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("MM月dd日 HH:mm")

    fun format(datetime: String?): String {
        if (datetime == null) return ""
        val time = OffsetDateTime.parse(datetime, DEFAULT_FORMATTER)
        return if (isThisYear(time))
            time.format(SHORT_DATETIME_FORMATTER)
        else
            time.format(DATETIME_FORMATTER)
    }

    fun ago(datetime: String): String {
        val given = LocalDateTime.parse(datetime, DEFAULT_FORMATTER)
        val now = LocalDateTime.now()
        val duration = Duration.between(given, now)
        // 根据差异计算结果
        return when {
            duration.seconds < 60 -> "${duration.seconds}秒前"
            duration.toMinutes() < 60 -> "${duration.toMinutes()}分钟前"
            duration.toHours() < 24 -> "${duration.toHours()}小时前"
            duration.toDays() < 7 -> "${duration.toDays()}天前"
            else -> format(datetime)
        }
    }

    private fun isThisYear(datetime: OffsetDateTime): Boolean {
        val now = OffsetDateTime.now()
        return datetime.year == now.year
    }
}
package com.azhu.basic.provider

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.CsvFormatStrategy
import com.orhanobut.logger.DiskLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.orhanobut.logger.Printer
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * @description:
 * @author: azhu
 * @date: 2024-10-01 14:20
 * @version: 1.0.0
 */
class LoggerProvider(private val on: Boolean) {

    companion object {
        fun init(debug: Boolean) {
            if (debug) {
                Logger.addLogAdapter(
                    AndroidLogAdapter(
                        PrettyFormatStrategy.newBuilder()
                            .showThreadInfo(false)
                            .methodCount(2)
                            .methodOffset(1)
                            .tag("v2ex")
                            .build()
                    )
                )
            } else {
                Logger.addLogAdapter(
                    DiskLogAdapter(
                        CsvFormatStrategy.newBuilder()
                            .tag("v2ex")
                            .dateFormat(SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.CHINA))
                            .build()
                    )
                )
            }
        }

        fun destory() {
            Logger.clearLogAdapters()
        }

        fun t(tag: String?): Printer {
            return Logger.t(tag)
        }

        fun log(priority: Int, tag: String?, message: String?, throwable: Throwable?) {
            Logger.log(priority, tag, message, throwable)
        }

        fun d(message: String, vararg args: Any?) {
            Logger.d(message, *args)
        }

        fun d(obj: Any?) {
            Logger.d(obj)
        }

        fun e(message: String, vararg args: Any?) {
            Logger.e(null, message, *args)
        }

        fun e(throwable: Throwable?, message: String, vararg args: Any?) {
            Logger.e(throwable, message, *args)
        }

        fun i(message: String, vararg args: Any?) {
            Logger.i(message, *args)
        }

        fun v(message: String, vararg args: Any?) {
            Logger.v(message, *args)
        }

        fun w(message: String, vararg args: Any?) {
            Logger.w(message, *args)
        }

        fun wtf(message: String, vararg args: Any?) {
            Logger.wtf(message, *args)
        }

        fun json(json: String?) {
            Logger.json(json)
        }

        fun xml(xml: String?) {
            Logger.xml(xml)
        }

        fun debug(msg: String) {
            Logger.d(msg)
        }

        fun i(msg: String) {
            Logger.i(msg)
        }
    }
}

val logger get() = LoggerProvider.Companion
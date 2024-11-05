package com.azhu.v2ex.http

/**
 * @author: Jerry
 * @date: 2024-10-13 21:01
 * @version: 1.0.0
 */
class UserAgentUtils {

    companion object {

        val UA_MOBILE by lazy { getUserAgent(true) }

        val UA_PC by lazy { return@lazy getUserAgent() }

        private fun getUserAgent(isMobile: Boolean = false): String {
            return if (isMobile) {
                "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.91 Mobile Safari/537.36"
            } else {
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36"
            }
//            val context = AppManager.getCurrentActivity()
//            if (context == null) {
//                return "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36"
//            } else {
//                return WebSettings.getDefaultUserAgent(context)
//            }
        }
    }
}
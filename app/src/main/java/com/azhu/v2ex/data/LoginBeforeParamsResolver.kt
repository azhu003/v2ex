package com.azhu.v2ex.data

import android.net.Uri
import com.azhu.v2ex.http.ApiException
import com.azhu.v2ex.utils.V2exUtils
import org.jsoup.nodes.Document

/**
 * @author: Jerry
 * @date: 2024-10-19 14:27
 * @version: 1.0.0
 */
class LoginBeforeParamsResolver : BaseResolver<LoginRequestParams>() {
    override fun resolver(document: Document): LoginRequestParams {
        val trs = document.select("form[action=/signin] tr")
        if (trs.size > 4) {
            val params = LoginRequestParams()
            params.username = str { trs[0].select("input.sl[type=text][name]").attr("name") }
            params.password = str { trs[1].select("input.sl[type=password][name]").attr("name") }
            params.captchaImageUrl = str {
                val url = V2exUtils.toAbsoluteURL(trs[2].select("img#captcha-image").attr("src"))
                Uri.parse(url).buildUpon()
                    .appendQueryParameter("now", System.currentTimeMillis().toString())
                    .build()
                    .toString()
            }
            params.captcha = str { trs[2].select("input.sl[type=text][name]").attr("name") }
            params.nonce = str { trs[3].select("input[name=once][value]").attr("value") }
            return params
        }
        throw ApiException("解析登录参数失败")
    }
}
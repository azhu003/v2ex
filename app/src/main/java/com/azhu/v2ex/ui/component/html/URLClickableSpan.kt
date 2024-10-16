package com.azhu.v2ex.ui.component.html

import android.net.Uri
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.graphics.toArgb
import com.azhu.basic.AppManager
import com.azhu.basic.provider.AppThemeProvider
import com.azhu.basic.provider.logger
import com.azhu.v2ex.ui.activity.TopicDetailsActivity
import com.azhu.v2ex.ui.activity.UserDetailsActivity
import com.azhu.v2ex.ui.theme.containerDark
import com.azhu.v2ex.ui.theme.containerLight
import com.azhu.v2ex.ui.theme.highlightsDark
import com.azhu.v2ex.ui.theme.highlightsLight
import com.azhu.v2ex.ui.theme.onContainerSecondaryDark
import com.azhu.v2ex.ui.theme.onContainerSecondaryLight
import com.azhu.v2ex.utils.RegexConstant
import com.azhu.v2ex.utils.V2exUtils

/**
 * @author: azhu
 * @date: 2024-10-10 18:17
 * @version: 1.0.0
 */
class URLClickableSpan(private val url: String) : ClickableSpan() {

    override fun onClick(widget: View) {
        AppManager.getCurrentActivity()?.let { context ->
            try {
                if (V2exUtils.isRelativeURL(url)) {
                    if (V2exUtils.isMemberUrl(url)) {
                        UserDetailsActivity.start(context, RegexConstant.MEMBER_USERNAME.find(url)?.value ?: "")
                    } else if (V2exUtils.isTopicUrl(url)) {
                        TopicDetailsActivity.start(context, RegexConstant.TOPIC_ID.find(url)?.value ?: "")
                    } else {
                        logger.error("未处理的站内链接跳转 -> $url")
                    }
                } else {
                    val schemeParams = CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(if (AppThemeProvider.isDark()) containerDark.toArgb() else containerLight.toArgb())
                        .setSecondaryToolbarColor(if (AppThemeProvider.isDark()) onContainerSecondaryDark.toArgb() else onContainerSecondaryLight.toArgb())
                        .build()
                    CustomTabsIntent.Builder()
                        .setDefaultColorSchemeParams(schemeParams)
                        .build()
                        .launchUrl(context, Uri.parse(url))
                }
            } catch (e: Exception) {
                logger.error("跳转链接失败 -> $e")
            }
        }
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.color = if (AppThemeProvider.isDark()) highlightsDark.toArgb() else highlightsLight.toArgb()
    }
}
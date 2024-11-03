package com.azhu.v2ex.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.ConfigurationCompat
import java.util.Locale

/**
 * @author: Jerry
 * @date: 2024-11-03 21:06
 * @version: 1.0.0
 */
@Composable
@ReadOnlyComposable
internal fun getString(@StringRes string: Int): String {
    LocalConfiguration.current
    val resources = LocalContext.current.resources
    return resources.getString(string)
}

@Composable
@ReadOnlyComposable
internal fun getString(@StringRes string: Int, vararg formatArgs: Any): String {
    val raw = getString(string)
    val locale = ConfigurationCompat.getLocales(LocalConfiguration.current).get(0) ?: Locale.getDefault()
    return String.format(locale, raw, *formatArgs)
}
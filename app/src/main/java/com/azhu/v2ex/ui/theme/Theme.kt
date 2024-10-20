package com.azhu.v2ex.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.azhu.basic.provider.AppTheme
import com.azhu.basic.provider.AppThemeProvider

data class CustomColorScheme(
    val primary: Color = Color(0xFF42A5F5), //主色
    val onPrimary: Color = Color(0xFFFFFFFF),  //用于主色背景上文字颜色
    val container: Color = Color(0xFFFFFFFF), //卡片等容器背景色
    val onContainerPrimary: Color = Color(0xFF181818),  //用于卡片等容器上重要文字颜色
    val onContainerSecondary: Color = Color(0xFFB2B2B2), //用于卡片等容器上次要文字颜色
    val background: Color = Color(0xFFEDEDED), //主背景色
    val onBackground: Color = Color(0xFF171717),  //用于显示在背景上的文字颜色
    val backgroundSecondary: Color = Color(0xFFF2F2F0),  //次要颜色的容器颜色
    val highlights: Color = Color(0xFF576B95),  //用于显示在背景上的文字颜色

    val onBackgroundSecondary: Color = Color(0xFFACB3B5),
)

private val CustomLightColorScheme = CustomColorScheme(
    container = containerLight,
    onContainerPrimary = onContainerPrimaryLight,
    onContainerSecondary = onContainerSecondaryLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    backgroundSecondary = backgroundSecondaryLight,
    highlights = highlightsLight
)

private val CustomDarkColorScheme = CustomColorScheme(
    container = containerDark,
    onContainerPrimary = onContainerPrimaryDark,
    onContainerSecondary = onContainerSecondaryDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    backgroundSecondary = onBackgroundSecondaryDark,
    highlights = highlightsDark
)

private val LocalCustomThemes = compositionLocalOf<CustomColorScheme> { error("No Color provided") }

val MaterialTheme.custom: CustomColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomThemes.current

private val LightColorScheme = lightColorScheme(
    primary = primaryColorLight,
    background = backgroundLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = primaryColorDark,
    background = backgroundDark
)

@Composable
fun ComposeV2exTheme(content: @Composable () -> Unit) {
    val colorScheme: ColorScheme
    val customColorScheme: CustomColorScheme
    when (AppThemeProvider.appTheme) {
        AppTheme.Light -> {
            colorScheme = LightColorScheme
            customColorScheme = CustomLightColorScheme
        }

        AppTheme.Dark -> {
            colorScheme = DarkColorScheme
            customColorScheme = CustomDarkColorScheme
        }
    }
    val context = LocalContext.current
    val rememberedDensity = remember {
        Density(
            density = context.resources.displayMetrics.widthPixels / 380f,
            fontScale = 1f
        )
    }
    CompositionLocalProvider(LocalDensity provides rememberedDensity, LocalCustomThemes provides customColorScheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
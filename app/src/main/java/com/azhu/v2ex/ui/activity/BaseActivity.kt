package com.azhu.v2ex.ui.activity

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.azhu.basic.provider.AppTheme
import com.azhu.basic.provider.AppThemeProvider
import com.azhu.v2ex.ui.component.AppTopBar
import com.azhu.v2ex.ui.component.FpsMonitor
import com.azhu.v2ex.ui.theme.ComposeV2exTheme
import com.azhu.v2ex.viewmodels.BaseViewModel

abstract class BaseActivity : ComponentActivity() {

    private val _vm by viewModels<BaseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initialize()
        setContent(getContentView())
    }

    private fun setContent(content: @Composable () -> Unit) {
        setContent(
            parent = null,
            content = {
                RootContentBox(
                    title = _vm.title.value,
                    onBackClick = { finish() },
                    content = content
                )
            }
        )
    }

    abstract fun getContentView(): @Composable () -> Unit

    protected open fun initialize() {

    }

    protected open fun isDisplayAppBar(): Boolean {
        return true
    }

    protected fun setAppBarTitle(title: String) {
        _vm.title.value = title
    }

    @Composable
    protected fun StatusBar(appTheme: AppTheme) {
        val context = LocalContext.current
        LaunchedEffect(key1 = appTheme == AppTheme.Dark) {
            if (context is Activity) {
                val systemBarsDarkIcon = when (appTheme) {
                    AppTheme.Light -> true
                    AppTheme.Dark -> false
                }
                val window = context.window
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = true
                } else {
                    window.statusBarColor = android.graphics.Color.TRANSPARENT
                    window.navigationBarColor = android.graphics.Color.TRANSPARENT
                }
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                    isAppearanceLightStatusBars = systemBarsDarkIcon
                    isAppearanceLightNavigationBars = systemBarsDarkIcon
                }
            }
        }
    }

    @Composable
    private fun RootContentBox(
        title: String = "",
        onBackClick: () -> Unit = {},
        content: @Composable () -> Unit
    ) {
        ComposeV2exTheme {
            StatusBar(AppThemeProvider.appTheme)
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    if (isDisplayAppBar()) {
                        Column {
                            AppTopBar(title, onBackClick)
                            HorizontalDivider(thickness = 0.15.dp)
                        }
                    }
                }) { pv ->
                if (isDisplayAppBar()) {
                    Box(modifier = Modifier.padding(pv)) {
                        content()
                        FpsMonitor(Modifier.align(Alignment.TopEnd))
                    }
                } else {
                    Box {
                        content()
                        FpsMonitor(
                            Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = pv.calculateTopPadding())
                        )
                    }
                }
            }
        }
    }
}
package com.azhu.v2ex.ui.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.azhu.basic.provider.AppTheme
import com.azhu.basic.provider.AppThemeProvider
import com.azhu.basic.provider.logger
import com.azhu.v2ex.ui.theme.ComposeV2exTheme
import com.azhu.v2ex.ui.theme.backgroundColorDark
import com.azhu.v2ex.ui.theme.custom
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
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                    isAppearanceLightStatusBars = systemBarsDarkIcon
                    isAppearanceLightNavigationBars = systemBarsDarkIcon
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
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
                            TopAppBar(
                                title = {
                                    Text(
                                        title, color = MaterialTheme.custom.onContainerPrimary, fontSize = TextUnit(
                                            20f,
                                            TextUnitType.Sp
                                        )
                                    )
                                },
                                colors = TopAppBarColors(
                                    containerColor = MaterialTheme.custom.container,
                                    titleContentColor = MaterialTheme.custom.onContainerPrimary,
                                    scrolledContainerColor = Color.Transparent,
                                    actionIconContentColor = Color.Transparent,
                                    navigationIconContentColor = MaterialTheme.custom.onContainerPrimary,
                                ),
                                navigationIcon = {
                                    IconButton(onClick = {
                                        onBackClick.invoke()
                                    }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null
                                        )
                                    }
                                }
                            )
                            HorizontalDivider(thickness = 0.15.dp)
                        }
                    }
                }) { pv ->
                if (isDisplayAppBar()) {
                    Box(modifier = Modifier.padding(pv)) {
                        content()
                    }
                } else {
                    content()
                }
            }
        }
    }
}
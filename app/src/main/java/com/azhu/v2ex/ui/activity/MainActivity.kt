package com.azhu.v2ex.ui.activity

import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.azhu.v2ex.ui.component.NavigationBar
import com.azhu.v2ex.viewmodels.HomeViewModel

class MainActivity : BaseActivity() {

    private val homeViewModel by viewModels<HomeViewModel>()

    override fun isDisplayAppBar(): Boolean {
        return false
    }

    override fun getContentView(): @Composable () -> Unit {
        return { NavigationBar(homeViewModel) }
    }
}
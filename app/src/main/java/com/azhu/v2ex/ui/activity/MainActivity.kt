package com.azhu.v2ex.ui.activity

import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.azhu.v2ex.ui.component.NavigationBar
import com.azhu.v2ex.viewmodels.TabTopicViewModel

class MainActivity : BaseActivity() {

    private val tabTopicViewModel by viewModels<TabTopicViewModel>()

    override fun isDisplayAppBar(): Boolean {
        return false
    }

    override fun getContentView(): @Composable () -> Unit {
        return { NavigationBar(tabTopicViewModel) }
    }
}
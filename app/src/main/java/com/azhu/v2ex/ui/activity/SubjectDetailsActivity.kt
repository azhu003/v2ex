package com.azhu.v2ex.ui.activity

import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.azhu.v2ex.viewmodels.SubjectDetailsViewModel

class SubjectDetailsActivity : BaseActivity() {

    private val vm by viewModels<SubjectDetailsViewModel>()

    override fun getContentView(): @Composable () -> Unit {
        return { SubjectDetailsPage(vm) }
    }

    override fun initialize() {
        super.initialize()
        super.setAppBarTitle("主题详情")
    }
}

@Composable
private fun SubjectDetailsPage(vm: SubjectDetailsViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("详情详情详情详情详情")
    }
}
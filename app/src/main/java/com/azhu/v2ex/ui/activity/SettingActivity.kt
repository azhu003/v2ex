package com.azhu.v2ex.ui.activity

import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.azhu.v2ex.R
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.viewmodels.SettingsViewModel

class SettingActivity : BaseActivity() {

    private val vm by viewModels<SettingsViewModel>()

    override fun initialize() {
        super.initialize()
        setAppBarTitle(getString(R.string.settings))
    }

    override fun getContentView(): @Composable () -> Unit {
        return { SettingPage(vm) }
    }

}

@Composable
private fun SettingPage(vm: SettingsViewModel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.custom.container)
                .padding(10.dp)
                .clickable { vm.logout() }
        ) {
            Text(text = context.getString(R.string.logout))
        }
    }
}
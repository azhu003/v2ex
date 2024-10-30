package com.azhu.v2ex.ui.activity

import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.azhu.v2ex.R
import com.azhu.v2ex.ui.component.LoadingDialog
import com.azhu.v2ex.ui.component.MessageDialog
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.viewmodels.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class SettingActivity : BaseActivity() {

    private val vm by viewModels<SettingsViewModel>()

    override fun initialize() {
        super.initialize()
        setAppBarTitle(getString(R.string.settings))
        vm.fetchData()
    }

    override fun getContentView(): @Composable () -> Unit {
        return { SettingPage(vm) }
    }

}

@Composable
private fun SettingPage(vm: SettingsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
    ) {
        if (vm.messageDialog.isDisplay) {
            MessageDialog(vm.messageDialog)
        }
        if (vm.loadingDialog.isDisplay) {
            LoadingDialog(vm.loadingDialog)
        }
        SettingItem(R.string.clear_catch, rightText = vm.cachedSize, onClick = { vm.cleanCache() })
        SettingItem(R.string.logout, onClick = { vm.logout() })
        SettingItem("弹一个Loading", onClick = {
            vm.loadingDialog.show()
            vm.viewModelScope.launch {
                delay(2000)
                vm.loadingDialog.dismiss()
            }
        })
        SettingItem("弹一个窗", onClick = {
            val sb = StringBuilder()
            val r = Random.nextInt(1, 20)
            sb.append("[${r}] ")
            repeat(r) {
                sb.append("这里是弹窗内容")
            }
            vm.messageDialog.show(title = "提示", message = sb.toString(),
                onNegativeClick = {
                    vm.toast("点击了取消")
                    it.dismiss()
                }, onPositiveClick = {
                    vm.toast("点击了确定")
                    it.dismiss()
                }, onDismiss = {
                    vm.toast("弹窗被关闭")
                }
            )
        })
    }
}

@Composable
private fun SettingItem(
    @StringRes text: Int,
    rightText: String? = null,
    @StringRes rightTextRes: Int? = null,
    onClick: (() -> Unit)? = null,
) {
    SettingItem(LocalContext.current.getString(text), rightText, rightTextRes, onClick)
}

@Composable
private fun SettingItem(
    text: String,
    rightText: String? = null,
    @StringRes rightTextRes: Int? = null,
    onClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.custom.container, MaterialTheme.shapes.small)
            .clickable(onClick != null) { onClick?.invoke() }
            .padding(horizontal = 10.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        val rText = if (!rightText.isNullOrBlank()) {
            rightText
        } else if (rightTextRes != null) {
            context.getString(rightTextRes)
        } else {
            null
        }
        if (rText != null) {
            Spacer(Modifier.weight(1f))
            Text(
                text = rText,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}
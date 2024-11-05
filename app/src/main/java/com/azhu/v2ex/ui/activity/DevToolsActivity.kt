package com.azhu.v2ex.ui.activity

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.azhu.v2ex.ext.clickableNoRipple
import com.azhu.v2ex.ui.component.LoadingDialog
import com.azhu.v2ex.ui.component.MessageDialog
import com.azhu.v2ex.ui.component.ReplyDialog
import com.azhu.v2ex.ui.component.ReplySheet
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.viewmodels.DevToolsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

class DevToolsActivity : BaseActivity() {

    private val vm by viewModels<DevToolsViewModel>()
    private lateinit var launcher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        launcher = registerForActivityResult(ActivityResultContracts.GetContent(), callback = vm.registerForActivityResult)
        super.onCreate(savedInstanceState)
    }

//    override fun isDisplayAppBar(): Boolean {
//        return false
//    }

    override fun initialize() {
        vm.replyDialog.onInsertImageClick = { launcher.launch("image/*") }
        repeat(54) {
            vm.replyDialog.emotions.add(String.format(Locale.ROOT, "file:///android_asset/emoticons/emoticon_%d.png", it + 1))
            vm.reply.emotions.add(String.format(Locale.ROOT, "file:///android_asset/emoticons/emoticon_%d.png", it + 1))
        }
    }

    override fun getContentView(): @Composable () -> Unit {
        return { DevToolsPage(vm) }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::launcher.isInitialized)
            launcher.unregister()
    }
}

@Composable
private fun DevToolsPage(vm: DevToolsViewModel) {
    val context = LocalContext.current
    val scrollableState = rememberScrollState()
    if (vm.messageDialog.isDisplay) {
        MessageDialog(vm.messageDialog)
    }
    if (vm.loadingDialog.isDisplay) {
        LoadingDialog(vm.loadingDialog)
    }

    Scaffold(bottomBar = { ReplySheet(vm.reply) }) { _ ->
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollableState)
                    .padding(horizontal = 15.dp)
            ) {
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
                SettingItem("弹一个回复窗", onClick = {
                    vm.replyDialog.isDisplay = true
                })
                SettingItem("弹一个回复窗 Next", onClick = {
                    vm.reply.isDisplay = true
                })
                SettingItem("读取emoji.html", onClick = {
                    vm.readEmoji()
                })
            }
            if (vm.reply.isDisplay) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clickableNoRipple {
                            vm.reply.isDisplay = false
                        }
                )
            }
        }
    }

    ReplyDialog(vm.replyDialog)
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
            .padding(top = 10.dp)
            .fillMaxWidth()
            .height(56.dp)
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
package com.azhu.v2ex.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.azhu.basic.AppManager
import com.azhu.v2ex.R
import com.azhu.v2ex.ui.theme.custom

/**
 * @author: Jerry
 * @date: 2024-10-30 16:29
 * @version: 1.0.0
 */
@Composable
fun LoadingDialog(state: LoadingDialogState) {
    DialogWrapper(
        Modifier.wrapContentSize(),
        state,
        DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false)
    ) {
        Box(Modifier.size(68.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.loading),
                contentDescription = LocalContext.current.getString(R.string.loading),
                tint = MaterialTheme.custom.onContainerSecondary,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun MessageDialog(state: MessageDialogState, properties: DialogProperties? = null) {
    DialogWrapper(Modifier.fillMaxWidth(0.75f), state, properties) {
        Text(
            text = state.message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.custom.onContainerPrimary,
            modifier = Modifier
                .padding(15.dp)
                .padding(top = 10.dp)
        )
    }
}

@Composable
fun <T : DialogState> DialogWrapper(
    modifier: Modifier? = Modifier,
    state: T,
    properties: DialogProperties? = null,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = { state.dismiss();state.onDismiss?.invoke() },
        properties = properties ?: DialogProperties(securePolicy = SecureFlagPolicy.SecureOff, usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 1.dp,
            shadowElevation = 1.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(MaterialTheme.custom.container, MaterialTheme.shapes.small)
                    .then(modifier ?: Modifier)
            ) {
                if (state.title.isNotEmpty()) {
                    Text(
                        text = state.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.custom.onContainerPrimary,
                        modifier = Modifier
                            .padding(15.dp)
                            .align(Alignment.Start)
                    )
                    HorizontalDivider(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        thickness = 0.1f.dp,
                        color = MaterialTheme.custom.background
                    )
                }
                content()
                if (state.onNegativeClick != null || state.onPositiveClick != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        thickness = 0.1f.dp,
                        color = MaterialTheme.custom.background
                    )
                    DialogActions(state)
                }
            }
        }
    }
}

@Composable
private fun DialogActions(state: DialogState) {
    val context = LocalContext.current
    Row {
        Spacer(modifier = Modifier.weight(1f))
        if (state.onNegativeClick != null) {
            Button(
                shape = MaterialTheme.shapes.small,
                colors = ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.custom.onContainerPrimary,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = MaterialTheme.custom.onContainerPrimary
                ),
                onClick = { state.onNegativeClick?.invoke(state) }
            ) {
                Text(context.getString(R.string.canceled), style = MaterialTheme.typography.bodyMedium)
            }
        }
        Button(
            shape = MaterialTheme.shapes.small,
            colors = ButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = MaterialTheme.custom.onContainerPrimary
            ),
            onClick = { state.onPositiveClick?.invoke(state) }) {
            Text(
                context.getString(R.string.positive),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.custom.primary
            )
        }
    }
}

@Composable
fun rememberDialogState(): DialogState {
    val state = DialogState()
    state.onNegativeClick = { it.dismiss() }
    state.onPositiveClick = { it.dismiss() }
    state.onDismiss = { state.dismiss() }
    return state
}

@Stable
open class DialogState : DialogDismiss {
    var title by mutableStateOf("")
    var isDisplay by mutableStateOf(false)
    var onDismiss: (() -> Unit)? = null
    var onNegativeClick: ((state: DialogState) -> Unit)? = null
    var onPositiveClick: ((state: DialogState) -> Unit)? = null

    fun show(
        title: String? = null,
        @StringRes titleRes: Int? = null,
        onDismiss: (() -> Unit)? = null,
        onNegativeClick: ((state: DialogState) -> Unit)? = null,
        onPositiveClick: ((state: DialogState) -> Unit)? = null,
    ) {
        this.title = title ?: if (titleRes !== null) AppManager.getCurrentActivity()?.getString(titleRes) ?: "" else ""
        this.onDismiss = onDismiss
        this.onNegativeClick = onNegativeClick
        this.onPositiveClick = onPositiveClick
        this.isDisplay = true
    }

    override fun dismiss() {
        this.isDisplay = false
        this.onNegativeClick = null
        this.onPositiveClick = null
    }
}

@Stable
class MessageDialogState : DialogState() {
    var message by mutableStateOf("")

    fun show(
        message: String? = null,
        @StringRes messageRes: Int? = null,
        title: String? = null,
        @StringRes titleRes: Int? = null,
        onDismiss: (() -> Unit)? = null,
        onNegativeClick: ((state: DialogState) -> Unit)? = null,
        onPositiveClick: ((state: DialogState) -> Unit)? = null,
    ) {
        super.show(
            title = title,
            titleRes = titleRes,
            onDismiss = onDismiss,
            onNegativeClick = onNegativeClick,
            onPositiveClick = onPositiveClick
        )
        if (!message.isNullOrEmpty()) this.message = message
        else if (messageRes != null) this.message = AppManager.getCurrentActivity()?.getString(messageRes) ?: ""
    }

    override fun dismiss() {
        super.dismiss()
        this.message = ""
    }
}

@Stable
class LoadingDialogState : DialogState() {
    fun show() {
        this.isDisplay = true
    }
}

interface DialogDismiss {
    fun dismiss()
}
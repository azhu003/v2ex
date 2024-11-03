package com.azhu.v2ex.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azhu.basic.provider.logger
import com.azhu.v2ex.ui.theme.custom

/**
 * @author: Jerry
 * @date: 2024-11-03 19:26
 * @version: 1.0.0
 */
private val DEFAULT_KEYBOARD_HEIGHT = 305.dp

@Composable
fun ReplySheet(state: ReplayDialogState) {
    if (!state.isDisplay) {
        state.currentInputAction = InputActions.NONE
        return
    }
    val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    LaunchedEffect(isKeyboardVisible) {
        state.isEmotionSheetExpanded = !isKeyboardVisible
    }

    var text by remember { mutableStateOf(TextFieldValue(state.content)) }
    val requester = remember { FocusRequester() }
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    BackHandler(enabled = state.currentInputAction != InputActions.NONE) {
        requester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.custom.container, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .padding(horizontal = 15.dp)
            .padding(top = 15.dp)
            .navigationBarsPadding()
    ) {
        TextField(
            value = text,
            onValueChange = { newValue -> text = newValue },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(8.dp),
            textStyle = TextStyle(fontSize = 14.sp),
            minLines = 2,
            maxLines = 6,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(requester)
                .background(MaterialTheme.custom.containerCard, MaterialTheme.shapes.small)
        )
        InputActions(
            selected = state.currentInputAction,
            onActionEvent = {
                if (it == InputActions.EMOJI) {
                    if (state.isEmotionSheetExpanded) {
                        softwareKeyboardController?.show()
                    } else {
                        softwareKeyboardController?.hide()
                    }
                }
                state.currentInputAction = it
            },
            submitEnable = true,
            onClickSubmit = state.onSubmit
        )
        ActionPanel(
            current = state.currentInputAction,
            onInputActionChanged = {
                state.currentInputAction = it
                requester.requestFocus()
            }, onEmotionClick = {
                logger.i("选中 emoji -> $it local=${getEmotionAssets(it)} url=${getEmotionUrl(it)}")
            }
        )
    }
}

@Composable
private fun ActionPanel(
    current: InputActions,
    onInputActionChanged: (action: InputActions) -> Unit,
    onEmotionClick: (emoji: String) -> Unit
) {
    var keyboardHeightDp by remember { mutableStateOf(value = 0.dp) }
    val ime = WindowInsets.ime
    val localDensity = LocalDensity.current
    val density = localDensity.density
    LaunchedEffect(density) {
        snapshotFlow { ime.getBottom(density = localDensity) }.collect {
            val realtimeKeyboardHeightDp = (it / density).dp
            keyboardHeightDp = maxOf(realtimeKeyboardHeightDp, keyboardHeightDp)
            if (realtimeKeyboardHeightDp == keyboardHeightDp) {
                onInputActionChanged(InputActions.NONE)
            }
        }
    }

    when (current) {
        InputActions.NONE, InputActions.Picture, InputActions.Link -> {
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .imePadding()
            )
        }

        InputActions.EMOJI -> {
            val maxHeight = if (keyboardHeightDp <= 0.dp) {
                DEFAULT_KEYBOARD_HEIGHT
            } else {
                keyboardHeightDp
            }
            Box(
                modifier = Modifier.heightIn(min = keyboardHeightDp, max = maxHeight)
            ) {
                EmotionTable(onEmotionClick)
            }
        }
    }
}
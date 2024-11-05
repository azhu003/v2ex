package com.azhu.v2ex.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.SecureFlagPolicy
import coil.compose.AsyncImage
import com.azhu.basic.provider.logger
import com.azhu.v2ex.R
import com.azhu.v2ex.ui.theme.custom

/**
 * use [ReplySheet] instead
 * @deprecated use {@link ReplayDialog} instead
 */
@OptIn(ExperimentalMaterial3Api::class)
@Deprecated("Deprecated")
@Composable
fun ReplyDialog(state: ReplayDialogState) {
    if (!state.isDisplay) return
    val sheet = rememberModalBottomSheetState()

    val requester = remember { FocusRequester() }
    var text by remember { mutableStateOf(TextFieldValue(state.content.toString())) }
    val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    LaunchedEffect(isKeyboardVisible) {
        if (isKeyboardVisible && !state.isEmotionSheetToggleByUser) {
            state.isEmotionSheetExpanded = false
        }
        state.isEmotionSheetToggleByUser = false
    }

    LaunchedEffect(state.isDisplay) {
        if (state.isDisplay) {
            sheet.show()
        } else {
            state.isEmotionSheetExpanded = false
            sheet.hide()
        }
    }
    // 显示时请求焦点弹出软键盘
    LaunchedEffect(sheet.isVisible) {
        if (sheet.isVisible) {
            requester.requestFocus()
        }
    }

    ModalBottomSheet(
        sheetState = sheet,
        onDismissRequest = { state.isDisplay = false },
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        containerColor = MaterialTheme.custom.container,
        scrimColor = Color.Black.copy(0.1f),
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.custom.onContainerSecondary) },
        properties = ModalBottomSheetProperties(securePolicy = SecureFlagPolicy.SecureOff),
    ) {
        Column(
            modifier = Modifier
                .heightIn(max = 300.dp)
                .padding(horizontal = 10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            TextField(
                value = text,
                onValueChange = { newValue ->
                    text = newValue
                },
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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {

                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(requester)
                    .background(MaterialTheme.custom.containerCard, MaterialTheme.shapes.small)
            )
            Actions(state)
            EmotionGrid(state)
        }
    }
}

@Composable
private fun Actions(state: ReplayDialogState) {
    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 10.dp)) {
        Icon(
            painter = if (state.isEmotionSheetExpanded) painterResource(R.drawable.keyboard) else painterResource(R.drawable.emoji),
            contentDescription = if (state.isEmotionSheetExpanded) "键盘" else "表情",
            tint = MaterialTheme.custom.icon,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    val isExpanded = state.isEmotionSheetExpanded.not()
                    logger.i("展开/收起 表情面板 -> old: ${state.isEmotionSheetExpanded} new: $isExpanded")
                    state.isEmotionSheetToggleByUser = true
                    state.isEmotionSheetExpanded = isExpanded
                    //显示表情面板时隐藏键盘，否则弹出键盘
                    if (isExpanded) {
                        keyboard?.hide()
                    } else {
                        keyboard?.show()
                    }
                }
        )
        Spacer(Modifier.width(15.dp))
        Icon(
            painter = painterResource(R.drawable.image),
            contentDescription = "图片",
            tint = MaterialTheme.custom.icon,
            modifier = Modifier
                .size(24.dp)
                .padding(1.dp)
                .clickable(onClick = state.onInsertImageClick)
        )
        Spacer(Modifier.width(15.dp))
        Icon(
            painter = painterResource(R.drawable.link),
            contentDescription = "链接",
            tint = MaterialTheme.custom.icon,
            modifier = Modifier
                .size(24.dp)
                .padding(2.dp)
                .clickable(onClick = state.onInsertLinkClick)
        )
        Spacer(Modifier.weight(1f))
        Button(
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.height(34.dp),
            onClick = state.onSubmit
        ) {
            Text(text = context.getString(R.string.replay), fontSize = 14.sp, color = Color.White)
        }
    }
}

@Composable
private fun EmotionGrid(state: ReplayDialogState) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(36.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
        items(state.emotions.size) {
            val item = state.emotions[it]
            key(it) {
                AsyncImage(
                    model = item,
                    contentDescription = null,
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {}
                )
            }
        }
    }
}

@Stable
class ReplayDialogState(
    val onInsertLinkClick: () -> Unit = {},
    val onSubmit: () -> Unit = {},
) {
    var onInsertImageClick: () -> Unit = {}
    var isDisplay by mutableStateOf(false)
    var content by mutableStateOf("")
    var isEmotionSheetExpanded by mutableStateOf(false)
    var isEmotionSheetToggleByUser by mutableStateOf(false)
    var emotions = mutableStateListOf<String>()
}

@Stable
class ReplaySheetState(
    val onInsertImage: () -> Unit,
    val onInsertEmoji: (emoji: String) -> Unit,
    val onInsertLink: (text: String, url: String) -> Unit,
    val onSubmit: () -> Unit,
) {
    var isDisplay by mutableStateOf(false)
    var content by mutableStateOf(TextFieldValue())
    var isEmotionSheetExpanded by mutableStateOf(false)
    var emotions = mutableStateListOf<String>()

    var currentInputAction by mutableStateOf(value = InputActions.NONE)

    fun insertToContent(pending: String) {
        val text = content
        val oldLength = text.text.length
        val sb = StringBuilder(text.text)
        val selection = text.selection
        val isSelected = text.selection.start < text.selection.end
        if (isSelected) {
            val newText = sb.replace(selection.start, selection.end, pending).toString()
            content = text.copy(newText, TextRange(selection.start + pending.length))
        } else {
            val newText = sb.insert(selection.start, pending).toString()
            content = text.copy(
                newText,
                TextRange(if (selection.start == oldLength) sb.length else selection.start + pending.length)
            )
        }
    }
}
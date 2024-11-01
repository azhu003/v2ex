package com.azhu.v2ex.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azhu.v2ex.R
import com.azhu.v2ex.ui.theme.custom

/**
 * @author: Jerry
 * @date: 2024-11-01 11:26
 * @version: 1.0.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplayDialog(state: ReplayDialogState) {
    val sheet = rememberModalBottomSheetState()
    val requester = remember { FocusRequester() }
    var text by remember { mutableStateOf(TextFieldValue(state.content)) }

    LaunchedEffect(state.isDisplay) {
        if (state.isDisplay) sheet.show() else sheet.hide()
    }
    // 显示时请求焦点弹出软键盘
    LaunchedEffect(sheet.isVisible) { if (sheet.isVisible) requester.requestFocus() }
    if (state.isDisplay) {
        ModalBottomSheet(
            sheetState = sheet,
            onDismissRequest = { state.isDisplay = false },
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            containerColor = MaterialTheme.custom.container,
            scrimColor = Color.Black.copy(0.1f),
            dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.custom.onContainerSecondary) }
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.Center
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
                Spacer(Modifier.height(8.dp))
                Actions(state)
            }
        }
    }
}

@Composable
private fun Actions(state: ReplayDialogState) {
    val context = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 10.dp)) {
        Icon(
            painter = painterResource(R.drawable.emoji),
            contentDescription = "表情",
            tint = MaterialTheme.custom.icon,
            modifier = Modifier
                .size(24.dp)
                .clickable {

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
                .padding(1.dp)
                .clickable(onClick = state.onInsertLinkClick)
        )
        Spacer(Modifier.weight(1f))
        Button(
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.height(28.dp),
            onClick = state.onSubmit
        ) {
            Text(text = context.getString(R.string.replay), fontSize = TextUnit(14f, TextUnitType.Sp), color = Color.White)
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
}
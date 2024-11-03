package com.azhu.v2ex.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azhu.v2ex.R
import com.azhu.v2ex.ext.clickableNoRipple
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.utils.getString

@Stable
enum class InputActions {
    NONE,
    EMOJI,
    Picture,
    Link,
}

@Composable
fun InputActions(
    selected: InputActions,
    onActionEvent: (InputActions) -> Unit,
    submitEnable: Boolean,
    onClickSubmit: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 10.dp)) {
        InputActionsButton(
            painter = if (selected == InputActions.EMOJI) painterResource(R.drawable.keyboard) else painterResource(R.drawable.emoji),
            contentDescription = if (selected == InputActions.EMOJI) "键盘" else "表情",
            onClick = { onActionEvent(InputActions.EMOJI) }
        )
        InputActionsButton(
            modifier = Modifier.padding(start = 16.dp),
            painter = painterResource(R.drawable.image),
            contentDescription = "图片",
            onClick = { onActionEvent(InputActions.Picture) }
        )
        InputActionsButton(
            modifier = Modifier.padding(start = 16.dp),
            painter = painterResource(R.drawable.link),
            contentDescription = "链接",
            onClick = { onActionEvent(InputActions.Link) }
        )
        Spacer(Modifier.weight(1f))
        Button(
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValues(0.dp),
            onClick = onClickSubmit,
            enabled = submitEnable,
            modifier = Modifier.height(34.dp)
        ) {
            Text(text = getString(R.string.replay), fontSize = 14.sp, color = Color.White)
        }
    }
}

@Composable
private fun InputActionsButton(
    modifier: Modifier = Modifier,
    painter: Painter,
    onClick: () -> Unit,
    contentDescription: String? = null
) {
    Icon(
        modifier = modifier
            .size(size = 26.dp)
            .clickableNoRipple(onClick = onClick),
        painter = painter,
        tint = MaterialTheme.custom.icon,
        contentDescription = contentDescription
    )
}

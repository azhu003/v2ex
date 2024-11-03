package com.azhu.v2ex.ui.component

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.azhu.v2ex.ext.findActivity

/**
 * 用于获取键盘高度的 Composable 函数。
 */
@Composable
fun observeKeyboardHeight(): Int {
    var keyboardHeight by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val context = LocalContext.current.findActivity() ?: return 0
    val rootView = remember { context.window.decorView.rootView }

    DisposableEffect(Unit) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height
            val heightDiff = screenHeight - rect.bottom
            keyboardHeight = if (heightDiff > screenHeight * 0.15) { // 阈值，用于判断软键盘是否弹出
                with(density) { heightDiff.toDp().roundToPx() }
            } else {
                0
            }
        }

        rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            rootView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    return keyboardHeight
}

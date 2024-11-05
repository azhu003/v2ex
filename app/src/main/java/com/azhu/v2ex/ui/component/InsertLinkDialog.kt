package com.azhu.v2ex.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azhu.v2ex.R
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.utils.getString

/**
 * @author: Jerry
 * @date: 2024-11-05 17:13
 * @version: 1.0.0
 */
@Composable
fun rememberLinkState() = remember { LinkState() }

@Composable
fun InsertLinkDialog(state: LinkState) {
    DialogWrapper(state) {
        InputField(R.string.insert_link_text, state.textField, KeyboardType.Text, focused = true)
        Spacer(Modifier.height(10.dp))
        InputField(R.string.insert_link_url, state.urlField, KeyboardType.Uri)
    }
}

@Composable
private fun InputField(
    @StringRes prefix: Int,
    text: MutableState<TextFieldValue>,
    keyboardType: KeyboardType,
    focused: Boolean = false
) {
    val requester = FocusRequester()
    TextField(
        value = text.value,
        onValueChange = { newValue -> text.value = newValue },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedPrefixColor = MaterialTheme.custom.onContainerPrimary,
            focusedPrefixColor = MaterialTheme.custom.onContainerPrimary,
            unfocusedTextColor = MaterialTheme.custom.onContainerPrimary,
            focusedTextColor = MaterialTheme.custom.onContainerPrimary,
        ),
        shape = RoundedCornerShape(8.dp),
        textStyle = TextStyle(fontSize = 14.sp),
        prefix = { Text(getString(prefix)) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .focusRequester(requester)
            .background(MaterialTheme.custom.containerCard, MaterialTheme.shapes.small)
    )

    LaunchedEffect(focused) {
        if (focused) {
            requester.requestFocus()
        }
    }
}

@Stable
class LinkState : DialogState() {
    val textField = mutableStateOf(TextFieldValue())
    val urlField = mutableStateOf(TextFieldValue())

    val text: String
        get() = textField.value.text.trim()

    val url: String
        get() = urlField.value.text.trim()

    fun reset() {
        textField.value = TextFieldValue()
        urlField.value = TextFieldValue()
    }
}
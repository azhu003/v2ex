package com.azhu.v2ex.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.azhu.v2ex.R
import com.azhu.v2ex.ui.theme.custom

/**
 * @author: Jerry
 * @date: 2024-10-19 16:38
 * @version: 1.0.0
 */
@Composable
fun LoadingLayout(
    state: LoadingState,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    if (state.state == LoadState.SUCCESS) {
        content()
    } else {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            val painter = when (state.state) {
                LoadState.LOADING -> painterResource(id = R.drawable.loading)
                LoadState.ERROR -> painterResource(id = R.drawable.load_error)
                else -> null
            }
            if (painter != null) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(48.dp)
                )
            }
            Text(
                text = when (state.state) {
                    LoadState.LOADING -> context.getString(R.string.loading)
                    LoadState.ERROR -> state.message
                    else -> ""
                },
                color = MaterialTheme.custom.onContainerSecondary,
                modifier = Modifier.padding(top = 10.dp)
            )
            if (state.state == LoadState.ERROR && onRetry != null) {
                Text(
                    text = context.getString(R.string.reload),
                    color = MaterialTheme.custom.onContainerPrimary,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(horizontal = 3.dp, vertical = 2.dp)
                        .clickable { onRetry.invoke() }
                )
            }
        }
    }
}

//@Composable
//fun rememberLoadingState(): LoadingState {
//    return remember { LoadingState() }
//}

@Stable
class LoadingState(state: LoadState = LoadState.LOADING) {

    var state by mutableStateOf(state)
        internal set

    var message by mutableStateOf("")

    fun setLoading() {
        this.state = LoadState.LOADING
    }

    fun setLoadError(message: String = "") {
        this.state = LoadState.ERROR
        this.message = message
    }

    fun setLoadSuccess() {
        this.state = LoadState.SUCCESS
        this.message = ""
    }

    fun setState(state: LoadState) {
        this.state = state
    }

    fun isLoading(): Boolean {
        return this.state == LoadState.LOADING
    }
}

@Stable
enum class LoadState {
    LOADING,
    SUCCESS,
    ERROR
}
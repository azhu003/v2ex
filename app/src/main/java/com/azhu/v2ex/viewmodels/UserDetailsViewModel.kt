package com.azhu.v2ex.viewmodels

import android.text.TextUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.azhu.basic.AppManager
import com.azhu.basic.provider.logger
import com.azhu.v2ex.R
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.UserDetails
import com.azhu.v2ex.ext.complete
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.ui.component.LoadingDialogState
import com.azhu.v2ex.ui.component.MessageDialogState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn

/**
 * @author: azhu
 * @date: 2024-10-11 15:54
 * @version: 1.0.0
 */
class UserDetailsViewModel : BaseViewModel() {

    var state by mutableStateOf(UserDetails())
        internal set

    val loadingDialogState = LoadingDialogState()
    val messageDialogState = MessageDialogState()

    fun fetchData() {
        if (TextUtils.isEmpty(state.username)) throw IllegalArgumentException("username is empty")

        http.flows { DataRepository.INSTANCE.getUserDetails(state.username) }
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error { logger.e(it?.message ?: "error message is null") }
            .success { state = it }
            .launchIn(viewModelScope)
    }

    fun flowing() {
        val context = AppManager.getCurrentActivity()
        if (state.action.flow.isNullOrEmpty() || context == null) {
            toast(R.string.operation_failed)
            return
        }
        val tips = if (state.isFollowed) {
            context.getString(R.string.unfollow_user_tips, state.username)
        } else {
            context.getString(R.string.follow_user_tips, state.username)
        }
        messageDialogState.show(message = tips, onNegativeClick = { it.dismiss() }, onPositiveClick = { dialog ->
            dialog.dismiss()
            loadingDialogState.show()
            http.flows { DataRepository.INSTANCE.toUserAction(state.action.flow!!) }
                .smap { Result.success(it) }
                .flowOn(Dispatchers.IO)
                .error { toast(it?.message ?: "操作失败") }
                .success { state = it }
                .complete { loadingDialogState.dismiss() }
                .launchIn(viewModelScope)
        })
    }
}
package com.azhu.v2ex.viewmodels

import android.text.TextUtils
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.UserDetails
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn

/**
 * @author: azhu
 * @date: 2024-10-11 15:54
 * @version: 1.0.0
 */
class UserDetailsViewModel : BaseViewModel() {

    val state = mutableStateOf(UserDetails())

    fun fetchData() {
        if (TextUtils.isEmpty(state.value.username)) throw IllegalArgumentException("username is empty")

        http.flows { DataRepository.INSTANCE.getUserDetails(state.value.username) }
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error { logger.e(it?.message ?: "error message is null") }
            .success { state.value = it }
            .launchIn(viewModelScope)
    }

}
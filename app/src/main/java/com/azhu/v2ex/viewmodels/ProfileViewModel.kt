package com.azhu.v2ex.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.UserProfile
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.ui.component.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn

/**
 * @author: Jerry
 * @date: 2024-10-18 12:01
 * @version: 1.0.0
 */
class ProfileViewModel : LazyLifecycleViewModel() {

    var profile by mutableStateOf(UserProfile(false))
        internal set

    var state by mutableStateOf(LoadingState())
        internal set

    override fun onLazyResume() {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        http.flows { DataRepository.INSTANCE.getUserProfile() }
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error {
                state.setLoadError("获取用户信息失败")
                logger.warning("获取用户信息失败 $it")
            }
            .success {
                profile = it
                state.setLoadSuccess()
            }
            .launchIn(viewModelScope)
    }

}
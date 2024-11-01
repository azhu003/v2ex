package com.azhu.v2ex.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import coil.annotation.ExperimentalCoilApi
import com.azhu.v2ex.R
import com.azhu.v2ex.data.SettingsState
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.http.Retrofits
import com.azhu.v2ex.utils.V2exUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn

/**
 * @author: Jerry
 * @date: 2024-10-21 21:13
 * @version: 1.0.0
 */
class SettingsViewModel : BaseViewModel() {

    val state = SettingsState()
    var cachedSize by mutableStateOf("")
        internal set

    fun fetchData() {
        fetchCatchSize()
    }

    fun logout() {
        if (!V2exUtils.isLogged() && V2exUtils.getCurrentUsername().isNullOrBlank()) return

        V2exUtils.logout()
        state.isLogoutSuccessfully.value = true
        toast(R.string.logout_succeed)

//        http.flows { DataRepository.INSTANCE.logout(V2exUtils.getCurrentUsername()!!, once) }
//            .smap { Result.success(it) }
//            .flowOn(Dispatchers.IO)
//            .error {
//                logger.warning("退出登录失败 $it")
//            }
//            .success {
//                V2exUtils.logout()
//                state.isLogoutSuccessfully.value = true
//            }
    }

    @OptIn(ExperimentalCoilApi::class)
    fun cleanCache() {
        http.flows {
            Retrofits.imageLoader.memoryCache?.clear()
            Retrofits.imageLoader.diskCache?.clear()
            delay(50)
        }
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .success {
                fetchCatchSize()
                toast(R.string.operation_successful)
            }
            .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun fetchCatchSize() {
        val size = Retrofits.imageLoader.diskCache?.size ?: 0
        cachedSize = V2exUtils.formatBytes(size)
    }
}
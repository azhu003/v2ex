package com.azhu.v2ex.viewmodels

import com.azhu.basic.provider.logger
import com.azhu.v2ex.R
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.SettingsState
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.utils.V2exUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

/**
 * @author: Jerry
 * @date: 2024-10-21 21:13
 * @version: 1.0.0
 */
class SettingsViewModel : BaseViewModel() {

    val state = SettingsState()

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

}
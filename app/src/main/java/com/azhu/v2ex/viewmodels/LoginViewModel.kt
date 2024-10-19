package com.azhu.v2ex.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.LoginRequestParams
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.ui.component.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * @author: Jerry
 * @date: 2024-10-20 00:24
 * @version: 1.0.0
 */
class LoginViewModel : BaseViewModel() {

    var state by mutableStateOf(LoadingState())
        internal set

    var params by mutableStateOf(LoginRequestParams())
        internal set

    var form by mutableStateOf(LoginRequestParams())

    fun fetchLoginParams() {
        http.flows { DataRepository.INSTANCE.getSigninParams() }
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error {
                state.setLoadError("获取用户信息失败")
                logger.warning("获取用户信息失败 $it")
            }
            .success {
                params = it
                state.setLoadSuccess()
            }
            .launchIn(viewModelScope)
    }

    fun login() {
        http.flows {
            DataRepository.INSTANCE.signin(
                Pair(params.username, form.username),
                Pair(params.password, form.password),
                Pair(params.captcha, form.captcha),
                params.nonce
            )
        }
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error {
                state.setLoadError("登录失败 ${it?.message}")
                logger.warning("登录失败 $it")
            }
            .success {
                state.setLoadSuccess()
                logger.warning("登录成功")
            }
            .launchIn(viewModelScope)
    }

    fun refreshCaptchaImage() {
        Uri.parse(params.captchaImageUrl)
            .buildUpon()
            .appendQueryParameter("p", LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli().toString())
            .build().toString().also {
                params.captchaImageUrl = it
            }
    }
}
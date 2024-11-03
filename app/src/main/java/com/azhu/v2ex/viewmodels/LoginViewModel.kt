package com.azhu.v2ex.viewmodels

import android.app.Activity
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.azhu.basic.AppManager
import com.azhu.basic.provider.StoreProvider
import com.azhu.basic.provider.logger
import com.azhu.v2ex.R
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.LoginRequestParams
import com.azhu.v2ex.data.LoginUiState
import com.azhu.v2ex.ext.complete
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.ui.component.LoadingDialogState
import com.azhu.v2ex.ui.component.LoadingState
import com.azhu.v2ex.utils.Constant
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

    val ui = LoginUiState()

    var state by mutableStateOf(LoadingState())
        internal set

    var params by mutableStateOf(LoginRequestParams())
        internal set

    var form by mutableStateOf(LoginRequestParams())

    var warning by mutableStateOf("")

    val loadingDialog = LoadingDialogState()

    fun fetchLoginParams() {
        ui.isLoading.value = false
        http.flows { DataRepository.INSTANCE.getSigninParams() }
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error {
                state.setLoadError("获取用户信息失败")
                logger.w("获取用户信息失败 $it")
            }
            .success {
                params = it
                state.setLoadSuccess()
            }
            .complete {
                ui.isLoading.value = true
            }
            .launchIn(viewModelScope)
    }

    fun login() {
        val context = AppManager.getCurrentActivity()
        if (context == null) {
            logger.e("登录失败，context 为空")
            return
        }
        when {
            form.username.isEmpty() -> {
                warning = context.getString(R.string.username_placeholder)
            }

            form.password.isEmpty() -> {
                warning = context.getString(R.string.password_placeholder)
            }

            form.captcha.isEmpty() -> {
                warning = context.getString(R.string.captcha_placeholder)
            }

            else -> {
                warning = ""
            }
        }
        if (warning.isNotEmpty()) return
        loadingDialog.show()
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
                warning = it?.message ?: ""
                fetchLoginParams()
                refreshCaptchaImage()
                logger.w("登录失败 $it")
            }
            .success {
                warning = ""
                StoreProvider.save(Constant.LOGGED_KEY, true)
                toast(context.getString(R.string.login_succeed))
                context.setResult(Activity.RESULT_OK)
                context.finish()
            }
            .complete { loadingDialog.dismiss() }
            .launchIn(viewModelScope)
    }

    fun refreshCaptchaImage() {
        Uri.parse(params.captchaImageUrl)
            .buildUpon()
            .appendQueryParameter("now", LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli().toString())
            .build().toString().also {
                params.captchaImageUrl = it
            }
    }
}
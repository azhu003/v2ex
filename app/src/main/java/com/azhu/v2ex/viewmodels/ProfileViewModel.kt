package com.azhu.v2ex.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.azhu.basic.AppManager
import com.azhu.basic.provider.StoreProvider
import com.azhu.basic.provider.logger
import com.azhu.v2ex.R
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.UserProfile
import com.azhu.v2ex.ext.complete
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.startActivityClass
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.http.KnownApiException
import com.azhu.v2ex.ui.activity.LoginActivity
import com.azhu.v2ex.ui.component.LoadingState
import com.azhu.v2ex.utils.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn

/**
 * @author: Jerry
 * @date: 2024-10-18 12:01
 * @version: 1.0.0
 */
class ProfileViewModel : LifecycleViewModel() {

    private var isRequestLogin = false

    var profile by mutableStateOf(UserProfile(false))
        internal set

    var state by mutableStateOf(LoadingState())
        internal set

    //是否点击领取连续登录奖励按钮
    var isClaimLoginRewardsEnable by mutableStateOf(false)

    var isRefreshingByUser by mutableStateOf(false)

    var isRepliesEmpty by mutableStateOf(false)
        internal set
    var isTopicEmpty by mutableStateOf(false)
        internal set

    override fun onLazyResume() {
        fetchUserProfile()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        val logged = StoreProvider.getBool(Constant.LOGGED_KEY)
        if (logged && isRequestLogin) {
            isRequestLogin = false
            state.setLoading()
            fetchUserProfile()
        }
    }

    private fun fetchUserProfile(isRefreshByUser: Boolean = false) {
        http.flows { DataRepository.INSTANCE.getUserProfile() }
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error {
                if (!isRefreshByUser) state.setLoadError("获取用户信息失败")
                logger.warning("获取用户信息失败 $it")
            }
            .success {
                profile = it
                isClaimLoginRewardsEnable = !it.isClaimedLoginRewards
                if (state.isNotSuccess()) state.setLoadSuccess()

                if (!profile.isUnlogged) {
                    fetchRecentlyActivities()
                }
            }
            .complete {
                isRefreshingByUser = false
            }
            .launchIn(viewModelScope)
    }

    private fun fetchRecentlyActivities() {
        http.flows { DataRepository.INSTANCE.getUserDetails(profile.username) }
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error { logger.error(it?.message ?: "error message is null") }
            .success {
                profile.topicInvisible = it.topicInvisible
                profile.topics.addAll(it.topics)
                profile.replies.addAll(it.replies)

                isTopicEmpty = profile.topics.isEmpty()
                isRepliesEmpty = profile.replies.isEmpty()
            }
            .launchIn(viewModelScope)
    }

    fun onRefresh() {
        isRefreshingByUser = true
        fetchUserProfile(true)
    }

    fun toLogin() {
        isRequestLogin = true
        AppManager.getCurrentActivity()?.startActivityClass(LoginActivity::class)
    }

    fun claimLoginRewards() {
        if (profile.isClaimedLoginRewards && profile.claimedLoginRewardNonce.isNullOrBlank()) {
            logger.info("已领取过奖励 once: ${profile.claimedLoginRewardNonce}")
            return
        }
        val context = AppManager.getCurrentActivity()
        http.flows(onRequestBefore = { isClaimLoginRewardsEnable = false }) {
            DataRepository.INSTANCE.claimLoginRewards(profile.claimedLoginRewardNonce!!)
        }
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error {
                logger.warning("领取连续登录奖励失败 $it")
                val msg = when (it) {
                    is KnownApiException -> it.message
                    else -> context?.getString(R.string.claim_login_rewards_failed)
                }
                if (msg != null) toast(msg)
            }
            .success {
                context?.let { context -> toast(context.getString(R.string.claim_login_rewards_succeed)) }
            }
            .complete {
                isClaimLoginRewardsEnable = !profile.isClaimedLoginRewards
            }
            .launchIn(viewModelScope)
    }
}
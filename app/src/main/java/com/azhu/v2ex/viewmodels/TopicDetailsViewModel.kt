package com.azhu.v2ex.viewmodels

import android.content.Context
import android.text.TextUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.azhu.basic.AppManager
import com.azhu.basic.provider.logger
import com.azhu.v2ex.R
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.TopicDetails
import com.azhu.v2ex.data.TopicDetailsResolverType
import com.azhu.v2ex.data.TopicReplyItem
import com.azhu.v2ex.ext.complete
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.ui.activity.UserDetailsActivity
import com.azhu.v2ex.ui.component.LoadingDialogState
import com.azhu.v2ex.ui.component.LoadingState
import com.azhu.v2ex.ui.component.MessageDialogState
import com.azhu.v2ex.utils.V2exUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn

/**
 * 主题详情
 * @author: azhu
 * @date: 2024-10-02 00:33
 * @version: 1.0.0
 */
class TopicDetailsViewModel : BaseViewModel() {

    val loading by mutableStateOf(LoadingState())  //初次加载时的Loading
    val loadingDialogState = LoadingDialogState()  //弹窗Loading
    val messageDialogState = MessageDialogState()

    var details by mutableStateOf(TopicDetails())
        internal set
    var isLoadingMoreData by mutableStateOf(false)
        internal set
    var hasMore by mutableStateOf(false)
        internal set

    //todo check re-login
    val isLogged by mutableStateOf(V2exUtils.isLogged())

    fun onViewUserClick(context: Context, item: TopicReplyItem) {
        UserDetailsActivity.start(context, item.username)
    }

    fun fetchTopicDetails(isLoadMore: Boolean = false) {
        if (TextUtils.isEmpty(details.tid)) {
            logger.error("topic id is null")
            return
        }
        if (isLoadMore) this.isLoadingMoreData = true
        else loading.setLoading()

        http.flows {
            DataRepository.INSTANCE.getTopicDetails(
                details.tid!!,
                if (isLoadMore) TopicDetailsResolverType.ONLY_REPLY else TopicDetailsResolverType.ALL,
                if (isLoadMore) details.replys.nextPage else 1
            )
        }.smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error {
                logger.error(it?.message ?: "error message is null")
                if (loading.isLoading()) {
                    AppManager.getCurrentActivity()?.let { context ->
                        loading.setLoadError(context.getString(R.string.load_failed))
                    }
                }
            }
            .success {
                merge(it, isLoadMore)
                if (loading.isLoading()) loading.setLoadSuccess()
            }
            .launchIn(viewModelScope)
    }

    fun thanks() {
        if (details.isThanked) {
            messageDialogState.show(messageRes = R.string.thanked_tips, onPositiveClick = { it.dismiss() })
            return
        }
        if (details.tid.isNullOrEmpty() || details.once.isNullOrEmpty()) {
            toast(R.string.operation_failed)
            return
        }
        messageDialogState.show(messageRes = R.string.thanks_topic_tips, onNegativeClick = { it.dismiss() }) { dialog ->
            dialog.dismiss()
            http.flows(
                onRequestBefore = { dialog.dismiss();loadingDialogState.show() },
                doRequest = {
                    DataRepository.INSTANCE.thank(details.tid!!, details.once!!)
                })
                .smap { Result.success(it) }
                .flowOn(Dispatchers.IO)
                .error { toast(R.string.operation_failed) }
                .success {
                    if (it.success == true) {
                        details.thanks = "${(details.thanks.toIntOrNull() ?: 0) + 1}"
                        details.isThanked = true
                        toast(R.string.thank_has_sent)
                    } else {
                        if (!it.message.isNullOrEmpty()) toast(it.message!!) else toast(R.string.operation_failed)
                    }
                    details.once = it.once.toString()
                }
                .complete { loadingDialogState.dismiss() }
                .launchIn(viewModelScope)
        }
    }

    fun thanksReply(item: TopicReplyItem) {
        val context = AppManager.getCurrentActivity()
        if (item.id.isEmpty() || details.once.isNullOrEmpty() || context == null) {
            toast(R.string.operation_failed)
            return
        }
        val message = context.getString(R.string.thanks_reply_tips, item.username)
        messageDialogState.show(message = message, onNegativeClick = { it.dismiss() }) { dialog ->
            dialog.dismiss()
            http.flows(
                onRequestBefore = { dialog.dismiss();loadingDialogState.show() },
                doRequest = {
                    DataRepository.INSTANCE.thankReplay(item.id, details.once!!)
                })
                .smap { Result.success(it) }
                .flowOn(Dispatchers.IO)
                .error { toast(R.string.operation_failed) }
                .success {
                    if (it.success == true) {
                        item.thanks = "${(item.thanks.toIntOrNull() ?: 0) + 1}"
                        toast(R.string.thank_has_sent)
                    } else {
                        if (!it.message.isNullOrEmpty()) toast(it.message!!) else toast(R.string.operation_failed)
                    }
                    details.once = it.once.toString()
                }
                .complete { loadingDialogState.dismiss() }
                .launchIn(viewModelScope)
        }
    }

    fun favorite() {
        if (details.tid.isNullOrEmpty() || details.once.isNullOrEmpty()) {
            toast(R.string.operation_failed)
            return
        }
        http.flows(
            onRequestBefore = { loadingDialogState.show() },
            doRequest = {
                if (details.isCollected) DataRepository.INSTANCE.unfavorite(
                    details.tid!!,
                    details.once!!
                ) else DataRepository.INSTANCE.favorite(
                    details.tid!!,
                    details.once!!
                )
            })
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error { toast(R.string.operation_failed) }
            .success {
                it.replys.page = details.replys.page
                it.replys.total = details.replys.total
                it.replys.data.addAll(details.replys.data)
                merge(it, false)
            }
            .complete { loadingDialogState.dismiss() }
            .launchIn(viewModelScope)
    }

    fun ignore() {
        if (details.tid.isNullOrEmpty() || details.once.isNullOrEmpty()) {
            toast(R.string.operation_failed)
            return
        }
        messageDialogState.show(messageRes = R.string.ignore_topic_tips, onNegativeClick = { it.dismiss() }) { dialog ->
            dialog.dismiss()
            http.flows(
                onRequestBefore = { dialog.dismiss(); loadingDialogState.show() },
                doRequest = {
                    DataRepository.INSTANCE.ignore(details.tid!!, details.once!!)
                })
                .smap { Result.success(it) }
                .flowOn(Dispatchers.IO)
                .error { toast(R.string.operation_failed) }
                .success {
                    it.replys.page = details.replys.page
                    it.replys.total = details.replys.total
                    it.replys.data.addAll(details.replys.data)
                    merge(it, false)
                }
                .complete { loadingDialogState.dismiss() }
                .launchIn(viewModelScope)
        }
    }

    fun replayAtUser(username: String) {
        toast("回复$username")
    }

    fun replayTopic() {
        toast("回复主题 ${details.tid}")
    }

    private fun merge(details: TopicDetails, isLoadMore: Boolean) {
        if (this.details.isInitialized && isLoadMore) {
            val old = this.details.replys
            val new = details.replys
            old.page = new.page
            old.total = new.total
            old.data.addAll(new.data)
            isLoadingMoreData = false
        } else {
            details.isInitialized = true
            this.details = details
        }
        hasMore = details.replys.page < details.replys.total
    }

}
package com.azhu.v2ex.viewmodels

import android.content.Context
import android.text.TextUtils
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.TopicDetails
import com.azhu.v2ex.data.TopicDetailsResolverType
import com.azhu.v2ex.data.TopicReplyItem
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.ui.activity.UserDetailsActivity
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

    val state = mutableStateOf(TopicDetails())
    val isLoadingMoreData = mutableStateOf(false)
    val hasMore = mutableStateOf(false)

    fun onViewUserClick(context: Context, item: TopicReplyItem) {
        UserDetailsActivity.start(context, item.username)
    }

    fun fetchTopicDetails(isLoadMore: Boolean = false) {
        val details = state.value
        if (TextUtils.isEmpty(details.tid)) {
            logger.error("topic id is null")
            return
        }
        if (isLoadMore) this.isLoadingMoreData.value = true
        http.flows {
            DataRepository.INSTANCE.getTopicDetails(
                details.tid!!,
                if (isLoadMore) TopicDetailsResolverType.ONLY_REPLY else TopicDetailsResolverType.ALL,
                if (isLoadMore) details.replys.nextPage else 1
            )
        }.smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error { logger.error(it?.message ?: "error message is null") }
            .success { merge(it, isLoadMore) }
            .launchIn(viewModelScope)
    }

    private fun merge(details: TopicDetails, loadMore: Boolean) {
        if (state.value.isInitialized && loadMore) {
            val old = state.value.replys
            val new = details.replys
            old.page = new.page
            old.total = new.total
            old.data.addAll(new.data)
            isLoadingMoreData.value = false
        } else {
            details.isInitialized = true
            state.value = details
        }
        hasMore.value = details.replys.page < details.replys.total
    }

}
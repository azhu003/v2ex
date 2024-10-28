package com.azhu.v2ex.viewmodels

import android.content.Context
import android.text.TextUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.azhu.basic.AppManager
import com.azhu.basic.provider.logger
import com.azhu.v2ex.R
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.TopicDetails
import com.azhu.v2ex.data.TopicDetailsResolverType
import com.azhu.v2ex.data.TopicReplyItem
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.ui.activity.UserDetailsActivity
import com.azhu.v2ex.ui.component.LoadingState
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

    val loading by mutableStateOf(LoadingState())
    val details = mutableStateOf(TopicDetails())
    val isLoadingMoreData = mutableStateOf(false)
    val hasMore = mutableStateOf(false)

    //todo check re-login
    val isLogged by mutableStateOf(V2exUtils.isLogged())

    fun onViewUserClick(context: Context, item: TopicReplyItem) {
        UserDetailsActivity.start(context, item.username)
    }

    fun fetchTopicDetails(isLoadMore: Boolean = false) {
        val details = details.value
        if (TextUtils.isEmpty(details.tid)) {
            logger.error("topic id is null")
            return
        }
        if (isLoadMore) this.isLoadingMoreData.value = true
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

    private fun merge(details: TopicDetails, loadMore: Boolean) {
        if (this.details.value.isInitialized && loadMore) {
            val old = this.details.value.replys
            val new = details.replys
            old.page = new.page
            old.total = new.total
            old.data.addAll(new.data)
            isLoadingMoreData.value = false
        } else {
            details.isInitialized = true
            this.details.value = details
        }
        hasMore.value = details.replys.page < details.replys.total
    }

}
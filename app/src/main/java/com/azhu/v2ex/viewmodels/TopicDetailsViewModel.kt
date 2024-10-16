package com.azhu.v2ex.viewmodels

import android.content.Context
import android.text.TextUtils
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.TopicDetails
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

    fun onViewUserClick(context: Context, item: TopicReplyItem) {
        UserDetailsActivity.start(context, item.username)
    }

    fun fetchTopicDetails() {
        val details = state.value
        if (TextUtils.isEmpty(details.sid)) throw NullPointerException("sid is null")

        http.flows { DataRepository.INSTANCE.getTopicDetails(details.sid!!) }
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error { logger.error(it?.message ?: "error message is null") }
            .success { state.value = it }
            .launchIn(viewModelScope)
    }

}
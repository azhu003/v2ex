package com.azhu.v2ex.ui.component

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.Topic
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.viewmodels.LifecycleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn

/**
 * @author: Jerry
 * @date: 2024-10-22 16:05
 * @version: 1.0.0
 */
class TopicCollectionViewModel : LifecycleViewModel() {

    val topics = mutableStateListOf<Topic>()

    override fun onLazyResume() {
        super.onLazyResume()
        fetchTopics()
    }

    private fun fetchTopics() {
        http.flows { DataRepository.INSTANCE.getTopicsFromCollection() }
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error { logger.info("加载话题收藏列表失败: $it") }
            .success { topics.addAll(it) }
            .launchIn(viewModelScope)
    }
}
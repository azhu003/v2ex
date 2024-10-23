package com.azhu.v2ex.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.TopicByNode
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.paging.TopicByNodeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn

/**
 * @author: Jerry
 * @date: 2024-10-23 12:34
 * @version: 1.0.0
 */
class TopicByNodeViewModel : BaseViewModel() {

    var node: String = ""
    var data by mutableStateOf(TopicByNode())
    val pager by lazy { Pager(PagingConfig(pageSize = 20)) { TopicByNodeSource(node) }.flow.cachedIn(viewModelScope) }

    fun fetchNodeInfo() {
        http.flows { DataRepository.INSTANCE.getTopicsByNode(node, onlyNodeInfo = true) }
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error { logger.info("获取节点主题失败 $it") }
            .success {
                this.data = it
            }
            .launchIn(viewModelScope)
    }

}
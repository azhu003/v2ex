package com.azhu.v2ex.ui.component

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.NodeInfo
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
class NodeCollectionViewModel : LifecycleViewModel() {

    val nodes = mutableStateListOf<NodeInfo>()

    override fun onLazyResume() {
        super.onLazyResume()
        fetchNodes()
    }

    private fun fetchNodes() {
        http.flows { DataRepository.INSTANCE.getNodesFromCollection() }
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error { logger.info("加载节点收藏列表失败: $it") }
            .success { nodes.addAll(it) }
            .launchIn(viewModelScope)
    }
}
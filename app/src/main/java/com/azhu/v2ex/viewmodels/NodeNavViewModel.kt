package com.azhu.v2ex.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.NodeNav
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.ui.component.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn

/**
 * @author: Jerry
 * @date: 2024-10-25 23:22
 * @version: 1.0.0
 */
class NodeNavViewModel : LifecycleViewModel() {

    val loading by mutableStateOf(LoadingState())
    var navMap by mutableStateOf<Map<String, List<NodeNav>>>(mapOf())
        internal set

    override fun onLazyResume() {
        super.onLazyResume()
        fetchData()
    }

    fun fetchData() {
        http.flows { DataRepository.INSTANCE.getNodeNavigation() }
            .smap { Result.success(it) }
            .flowOn(Dispatchers.IO)
            .error { loading.setLoadError(it?.message ?: "Load error") }
            .success {
                navMap = it
                logger.i("navMap: $navMap")
                loading.setLoadSuccess()
            }
            .launchIn(viewModelScope)
    }
}
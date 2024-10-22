package com.azhu.v2ex.ui.component

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.azhu.v2ex.paging.FollowingTopicListSource
import com.azhu.v2ex.viewmodels.LifecycleViewModel

/**
 * @author: Jerry
 * @date: 2024-10-22 16:05
 * @version: 1.0.0
 */
class FollowingViewModel : LifecycleViewModel() {

    val pager = Pager(config = PagingConfig(20), pagingSourceFactory = { FollowingTopicListSource() })
        .flow.cachedIn(viewModelScope)

}
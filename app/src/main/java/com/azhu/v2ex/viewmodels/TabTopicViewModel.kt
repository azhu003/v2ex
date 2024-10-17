package com.azhu.v2ex.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.azhu.v2ex.data.TabPair
import com.azhu.v2ex.data.Topic
import com.azhu.v2ex.paging.TopicListSource
import kotlinx.coroutines.flow.Flow

/**
 * @description:
 * @author: azhu
 * @date: 2024-09-29 22:23
 * @version: 1.0.0
 */
class TabTopicViewModel : BaseViewModel() {

    private val _isRefreshing: MutableState<Boolean> = mutableStateOf(false)
    val isRefreshing: Boolean get() = _isRefreshing.value

    val tabs: SnapshotStateList<TabPair> = mutableStateListOf()
    private val mPagerMap: MutableMap<String, Flow<PagingData<Topic>>> = mutableMapOf()

    init {
        tabs.add(TabPair("recent", "最近"))
        tabs.add(TabPair("tach", "技术"))
        tabs.add(TabPair("creative", "创意"))
        tabs.add(TabPair("play", "好玩"))
        tabs.add(TabPair("apple", "Apple"))
        tabs.add(TabPair("jobs", "酷工作"))
        tabs.add(TabPair("deals", "交易"))
        tabs.add(TabPair("city", "城市"))
        tabs.add(TabPair("qna", "问与答"))
        tabs.add(TabPair("hot", "最热"))
        tabs.add(TabPair("all", "全部"))
        tabs.add(TabPair("r2", "R2"))
        tabs.add(TabPair("xna", "VXNA"))
        tabs.add(TabPair("tabs", "节点"))
        tabs.add(TabPair("members", "关注"))
    }

    fun getPager(page: Int): Flow<PagingData<Topic>> {
        val tabKey = tabs[page].key
        return mPagerMap.getOrPut(tabKey) {
            Pager(config = PagingConfig(25), pagingSourceFactory = { TopicListSource(tabKey) })
                .flow
                .cachedIn(viewModelScope)
        }
    }

    fun setPullRefreshState() {
        _isRefreshing.value = true
    }

    fun setPullRefreshComplete() {
        _isRefreshing.value = false
    }
}
package com.azhu.v2ex.data

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.azhu.v2ex.viewmodels.LifecycleViewModel

/**
 * @author: Jerry
 * @date: 2024-10-25 17:47
 * @version: 1.0.0
 */
abstract class ListViewModel<T : Any> : LifecycleViewModel() {

    private val _pager by lazy {
        Pager(
            config = PagingConfig(getPageSize()),
            pagingSourceFactory = { getPagingSource() }).flow.cachedIn(viewModelScope)
    }

    val pager = _pager

    protected abstract fun getPagingSource(): PagingSource<Int, T>

    protected abstract fun getPageSize(): Int
}
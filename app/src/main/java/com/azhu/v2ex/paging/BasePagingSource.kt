package com.azhu.v2ex.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.Pagination

/**
 * @author: Jerry
 * @date: 2024-10-25 18:04
 * @version: 1.0.0
 */
abstract class BasePagingSource<T : Any> : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        try {
            val page = params.key ?: 1
            val result = getRemoteData(page)
            val prevKey = if (page == 1) null else page - 1
            val nextPage = if (page < result.total) page + 1 else null
            return LoadResult.Page(result.data, prevKey = prevKey, nextKey = nextPage)
        } catch (e: Exception) {
            logger.warning("加载列表数据失败 $e")
            return LoadResult.Error(e)
        }
    }

    protected abstract suspend fun getRemoteData(page: Int): Pagination<T>
}
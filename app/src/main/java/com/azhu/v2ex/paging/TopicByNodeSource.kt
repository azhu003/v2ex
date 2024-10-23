package com.azhu.v2ex.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.Topic
import com.azhu.v2ex.http.KnownApiException

/**
 * @author: Jerry
 * @date: 2024-10-23 12:46
 * @version: 1.0.0
 */
class TopicByNodeSource(private val node: String) : PagingSource<Int, Topic>() {

    override fun getRefreshKey(state: PagingState<Int, Topic>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Topic> {
        try {
            if (node.isEmpty()) throw KnownApiException("node is empty")

            val page = params.key ?: 1
            val result = DataRepository.INSTANCE.getTopicsByNode(node, page, onlyTopic = true)
            val prevKey = if (page == 1) null else page - 1
            val nextPage = if (page < result.pagination.total) page + 1 else null
            return LoadResult.Page(result.pagination.data, prevKey = prevKey, nextKey = nextPage)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

}
package com.azhu.v2ex.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.TopicDetailsResolverType
import com.azhu.v2ex.data.TopicReplyItem

/**
 * @author: Jerry
 * @date: 2024-10-17 02:06
 * @version: 1.0.0
 */
class TopicReplySource(private val tid: String) : PagingSource<Int, TopicReplyItem>() {

    override fun getRefreshKey(state: PagingState<Int, TopicReplyItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TopicReplyItem> {
        try {
            val page = params.key ?: 1

            val details = DataRepository.INSTANCE.getTopicDetails(tid, TopicDetailsResolverType.ONLY_REPLY)
            val pagination = details.replys

            val prevKey = if (page == 1) null else page - 1
            val nextPage = if (page < pagination.total) page + 1 else null
            return LoadResult.Page(pagination.data, prevKey = prevKey, nextKey = nextPage)
        } catch (e: Exception) {
            logger.warning("主题详情回复列表加载出错 $e")
            return LoadResult.Error(e)
        }
    }
}
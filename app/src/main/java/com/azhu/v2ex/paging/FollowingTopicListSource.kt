package com.azhu.v2ex.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.Topic

/**
 * @author: Jerry
 * @date: 2024-10-22 23:44
 * @version: 1.0.0
 */
class FollowingTopicListSource : PagingSource<Int, Topic>() {

    override fun getRefreshKey(state: PagingState<Int, Topic>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Topic> {
        try {
            val page = params.key ?: 1
            val pagination = DataRepository.INSTANCE.getFollowings(page)
            val prevKey = if (page == 1) null else page - 1
            val nextPage = if (page < pagination.total) page + 1 else null

            return LoadResult.Page(pagination.data, prevKey = prevKey, nextKey = nextPage)
        } catch (e: Exception) {
            logger.warning("关注用户主题列表加载出错 $e")
            return LoadResult.Error(e)
        }
    }
}
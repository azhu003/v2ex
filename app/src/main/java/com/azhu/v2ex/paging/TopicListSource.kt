package com.azhu.v2ex.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.Topic

/**
 * 主题列表分页
 * @author: Jerry
 * @date: 2024-10-16 02:15
 * @version: 1.0.0
 */
class TopicListSource(private val tabName: String) : PagingSource<Int, Topic>() {

    override fun getRefreshKey(state: PagingState<Int, Topic>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Topic> {
        try {
            val page = params.key ?: 1

            val repository = DataRepository.INSTANCE
            val pagination = if (tabName == "recent")
                repository.getRecentTopicList(page)
            else
                repository.getTopicListData(tabName)

            val prevKey = if (page == 1) null else page - 1
            val nextPage = if (page < pagination.total) page + 1 else null
            return LoadResult.Page(pagination.data, prevKey = prevKey, nextKey = nextPage)
        } catch (e: Exception) {
            logger.warning("主题列表加载出错 $e")
            return LoadResult.Error(e)
        }
    }
}
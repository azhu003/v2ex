package com.azhu.v2ex.paging

import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.Pagination
import com.azhu.v2ex.data.Topic

/**
 * 主题列表分页
 * @author: Jerry
 * @date: 2024-10-16 02:15
 * @version: 1.0.0
 */
class TopicListSource(private val tabName: String) : BasePagingSource<Topic>() {

    override suspend fun getRemoteData(page: Int): Pagination<Topic> {
        val repository = DataRepository.INSTANCE
        return when (tabName) {
            "recent" -> repository.getRecentTopicList(page)
            else -> repository.getTopicListData(tabName)
        }
    }
}
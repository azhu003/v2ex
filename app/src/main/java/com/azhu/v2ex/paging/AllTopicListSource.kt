package com.azhu.v2ex.paging

import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.Pagination
import com.azhu.v2ex.data.Topic

/**
 * @author: Jerry
 * @date: 2024-10-25 17:41
 * @version: 1.0.0
 */
class AllTopicListSource(private val username: String) : BasePagingSource<Topic>() {

    override suspend fun getRemoteData(page: Int): Pagination<Topic> {
        return DataRepository.INSTANCE.getAllTopicByUser(username, page)
    }

}
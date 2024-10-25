package com.azhu.v2ex.paging

import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.Pagination
import com.azhu.v2ex.data.Topic
import com.azhu.v2ex.http.KnownApiException

/**
 * @author: Jerry
 * @date: 2024-10-23 12:46
 * @version: 1.0.0
 */
class TopicByNodeSource(private val node: String) : BasePagingSource<Topic>() {

    override suspend fun getRemoteData(page: Int): Pagination<Topic> {
        if (node.isEmpty()) throw KnownApiException("node is empty")
        return DataRepository.INSTANCE.getTopicsByNode(node, page, onlyTopic = true).pagination
    }

}
package com.azhu.v2ex.paging

import com.azhu.v2ex.data.DataRepository
import com.azhu.v2ex.data.Pagination
import com.azhu.v2ex.data.UserRecentlyReply

/**
 * @author: Jerry
 * @date: 2024-10-25 17:41
 * @version: 1.0.0
 */
class RepliesSource(private val username: String) : BasePagingSource<UserRecentlyReply>() {

    override suspend fun getRemoteData(page: Int): Pagination<UserRecentlyReply> {
        return DataRepository.INSTANCE.getAllRepliesByUser(username, page)
    }

}
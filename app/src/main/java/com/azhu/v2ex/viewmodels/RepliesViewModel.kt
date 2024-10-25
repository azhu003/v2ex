package com.azhu.v2ex.viewmodels

import androidx.paging.PagingSource
import com.azhu.v2ex.data.ListViewModel
import com.azhu.v2ex.data.UserRecentlyReply
import com.azhu.v2ex.paging.RepliesSource

/**
 * @author: Jerry
 * @date: 2024-10-25 17:39
 * @version: 1.0.0
 */
class RepliesViewModel : ListViewModel<UserRecentlyReply>() {

    var username: String = ""

    override fun getPagingSource(): PagingSource<Int, UserRecentlyReply> {
        return RepliesSource(username)
    }

    override fun getPageSize(): Int {
        return 20
    }

}
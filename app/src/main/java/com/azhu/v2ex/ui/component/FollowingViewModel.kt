package com.azhu.v2ex.ui.component

import androidx.paging.PagingSource
import com.azhu.v2ex.data.Topic
import com.azhu.v2ex.data.ListViewModel
import com.azhu.v2ex.paging.FollowingTopicListSource

/**
 * @author: Jerry
 * @date: 2024-10-22 16:05
 * @version: 1.0.0
 */
class FollowingViewModel : ListViewModel<Topic>() {

    override fun getPagingSource(): PagingSource<Int, Topic> {
        return FollowingTopicListSource()
    }

    override fun getPageSize(): Int {
        return 20
    }

}
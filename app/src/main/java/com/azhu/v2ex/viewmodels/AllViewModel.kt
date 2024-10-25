package com.azhu.v2ex.viewmodels

import androidx.paging.PagingSource
import com.azhu.v2ex.paging.AllTopicListSource
import com.azhu.v2ex.data.Topic
import com.azhu.v2ex.data.ListViewModel

/**
 * @author: Jerry
 * @date: 2024-10-25 17:39
 * @version: 1.0.0
 */
class AllViewModel : ListViewModel<Topic>() {

    var username: String = ""

    override fun getPagingSource(): PagingSource<Int, Topic> {
        return AllTopicListSource(username)
    }

    override fun getPageSize(): Int {
        return 20
    }

}
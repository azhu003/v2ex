package com.azhu.v2ex.ui.activity

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.paging.compose.collectAsLazyPagingItems
import com.azhu.v2ex.R
import com.azhu.v2ex.ui.component.TopicItem
import com.azhu.v2ex.viewmodels.AllViewModel

class AllTopicActivity : BaseActivity() {

    companion object {
        fun start(context: Context, username: String) {
            context.startActivity(Intent(context, AllTopicActivity::class.java).putExtra("username", username))
        }
    }

    private val vm by viewModels<AllViewModel>()

    override fun initialize() {
        super.initialize()
        val username = intent.getStringExtra("username")
        if (username.isNullOrEmpty()) {
            finish()
            return
        }
        setAppBarTitle(getString(R.string.all_topic))
        vm.username = username
    }

    override fun getContentView(): @Composable () -> Unit {
        return { MoreTopicPage(vm) }
    }
}

@Composable
private fun MoreTopicPage(vm: AllViewModel) {
    val data = vm.pager.collectAsLazyPagingItems()

    LazyColumn {
        items(data.itemCount) {
            val item = data[it]
            if (item != null) {
                key(item.id) { TopicItem(item) }
            }
        }
    }
}
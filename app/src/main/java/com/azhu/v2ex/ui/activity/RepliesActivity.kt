package com.azhu.v2ex.ui.activity

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.azhu.v2ex.R
import com.azhu.v2ex.ui.component.RecentlyReplyItem
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.viewmodels.RepliesViewModel

class RepliesActivity : BaseActivity() {

    companion object {
        fun start(context: Context, username: String) {
            context.startActivity(Intent(context, RepliesActivity::class.java).putExtra("username", username))
        }
    }

    private val vm by viewModels<RepliesViewModel>()

    override fun initialize() {
        super.initialize()
        val username = intent.getStringExtra("username")
        if (username.isNullOrBlank()) {
            finish()
            return
        }
        setAppBarTitle(getString(R.string.all_reply))
        vm.username = username
    }

    override fun getContentView(): @Composable () -> Unit {
        return { RepliesPage(vm) }
    }
}

@Composable
private fun RepliesPage(vm: RepliesViewModel) {
    val state = rememberLazyListState()
    val data = vm.pager.collectAsLazyPagingItems()
    LazyColumn(
        state = state,
        modifier = Modifier
            .padding(8.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.custom.container)
    ) {
        items(data.itemCount) {
            val reply = data[it]
            if (reply != null) {
                key(it) { RecentlyReplyItem(reply, it == data.itemCount - 1) }
            }
        }
    }
}
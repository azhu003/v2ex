package com.azhu.v2ex.ui.activity

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.azhu.v2ex.R
import com.azhu.v2ex.ui.component.TopicItem
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.viewmodels.TopicByNodeViewModel

class TopicByNodeActivity : BaseActivity() {

    companion object {
        fun start(context: Context, node: String) {
            val intent = Intent(context, TopicByNodeActivity::class.java)
            intent.putExtra("node", node)
            context.startActivity(intent)
        }
    }

    private val vm by viewModels<TopicByNodeViewModel>()

    override fun initialize() {
        super.initialize()
        val node = intent.getStringExtra("node")
        if (node.isNullOrBlank()) {
            finish()
            return
        }
        vm.node = node
        vm.fetchNodeInfo()
    }

    override fun getContentView(): @Composable () -> Unit {
        return {
            NodeTopicPage(vm)
        }
    }
}

@Composable
private fun NodeTopicPage(vm: TopicByNodeViewModel) {
    val context = LocalContext.current
    val data = vm.pager.collectAsLazyPagingItems()

    LazyColumn {
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.custom.container)
                    .padding(10.dp)
            ) {
                AsyncImage(
                    model = vm.data.nodeImage,
                    contentDescription = null,
                    contentScale = ContentScale.Inside,
                    modifier = Modifier.size(60.dp)
                )
                Column(Modifier.padding(start = 10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = vm.data.nodeName,
                            color = MaterialTheme.custom.onContainerPrimary,
                            fontSize = 14.sp,
                            modifier = Modifier.alignByBaseline()
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "${context.getString(R.string.topic_total)} ${vm.data.comments}",
                            color = MaterialTheme.custom.onContainerSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .alignByBaseline()
                        )
                    }
                    Text(
                        text = vm.data.intro,
                        color = MaterialTheme.custom.onContainerSecondary,
                        fontSize = 10.sp,
                        lineHeight = 12.sp,
                    )
                }
            }
        }
        items(data.itemCount) {
            val item = data[it]
            if (item != null) {
                key(item.id) { TopicItem(item) }
            }
        }
    }
}
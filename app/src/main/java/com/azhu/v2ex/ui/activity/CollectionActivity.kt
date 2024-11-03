package com.azhu.v2ex.ui.activity

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.azhu.v2ex.R
import com.azhu.v2ex.ui.component.FollowingViewModel
import com.azhu.v2ex.ui.component.NodeCollectionViewModel
import com.azhu.v2ex.ui.component.ObserveLifecycleLayout
import com.azhu.v2ex.ui.component.TabPager
import com.azhu.v2ex.ui.component.TopicCollectionViewModel
import com.azhu.v2ex.ui.component.TopicItem
import com.azhu.v2ex.ui.theme.custom

class CollectionActivity : BaseActivity() {

    companion object {
        fun start(context: Context, page: Int) {
            val intent = Intent(context, CollectionActivity::class.java)
            intent.putExtra("page", page)
            context.startActivity(intent)
        }
    }

    private val nVM by viewModels<NodeCollectionViewModel>()
    private val tVM by viewModels<TopicCollectionViewModel>()
    private val fVM by viewModels<FollowingViewModel>()
    private var selectedTabIndex by mutableIntStateOf(0)

    override fun initialize() {
        super.initialize()
        setAppBarTitle(getString(R.string.my_collection))
        selectedTabIndex = intent.getIntExtra("page", 0)
    }

    override fun getContentView(): @Composable () -> Unit {
        return { CollectionPage(nVM, tVM, fVM, selectedTabIndex) }
    }
}

@Composable
private fun CollectionPage(
    nVM: NodeCollectionViewModel,
    tVM: TopicCollectionViewModel,
    fVM: FollowingViewModel,
    selectedTabIndex: Int
) {
    val context = LocalContext.current
    TabPager(tabArray = context.resources.getStringArray(R.array.collection_tab), selectedTabIndex = selectedTabIndex) {
        when (it) {
            0 -> NodeCollection(nVM)
            1 -> TopicCollection(tVM)
            2 -> Following(fVM)
        }
    }
}

@Composable
private fun NodeCollection(vm: NodeCollectionViewModel) {
    val nodes = vm.nodes
    val context = LocalContext.current
    ObserveLifecycleLayout(vm) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 5.dp, horizontal = 10.dp)
                .background(MaterialTheme.custom.container, MaterialTheme.shapes.extraSmall)
                .padding(10.dp)
        ) {
            items(nodes.size) { index ->
                val node = nodes[index]
                key(node.name) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { TopicByNodeActivity.start(context, node.key) }) {

                        AsyncImage(
                            model = node.image,
                            contentDescription = null,
                            contentScale = ContentScale.Inside,
                            modifier = Modifier.size(40.dp)
                        )

                        Text(
                            text = node.name,
                            color = MaterialTheme.custom.onContainerPrimary,
                            fontSize = 14.sp,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(R.drawable.conversation), contentDescription = null)
                            Text(
                                text = node.comments,
                                color = MaterialTheme.custom.onContainerSecondary,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicCollection(vm: TopicCollectionViewModel) {
    ObserveLifecycleLayout(vm) {
        LazyColumn(state = rememberLazyListState(), modifier = Modifier.fillMaxSize()) {
            items(vm.topics) { topic ->
                key(topic.id) {
                    TopicItem(topic)
                }
            }
        }
    }
}

@Composable
private fun Following(vm: FollowingViewModel) {
    val data = vm.pager.collectAsLazyPagingItems()
    ObserveLifecycleLayout(vm) {
        LazyColumn(state = rememberLazyListState(), modifier = Modifier.fillMaxSize()) {
            items(data.itemCount) { index ->
                val topic = data[index]
                if (topic != null) {
                    key(topic.id) {
                        TopicItem(topic)
                    }
                }
            }
        }
    }
}
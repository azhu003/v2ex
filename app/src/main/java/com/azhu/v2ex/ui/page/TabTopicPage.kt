@file:OptIn(ExperimentalMaterial3Api::class)

package com.azhu.v2ex.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.azhu.v2ex.R
import com.azhu.v2ex.data.Topic
import com.azhu.v2ex.ui.activity.TopicDetailsActivity
import com.azhu.v2ex.ui.activity.UserDetailsActivity
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.viewmodels.TabTopicViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabTopicPage(vm: TabTopicViewModel) {
    val hPagerState = rememberPagerState { vm.tabs.size }
    val coroutineScope = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()

    val data = vm.getPager(hPagerState.currentPage).collectAsLazyPagingItems()

    Scaffold { pv ->
        Column {
            // 👇🏻 这个Row组件用来填充系统状态栏使状态栏和TabRow颜色保持一致
            Row(
                modifier = Modifier
                    .background(MaterialTheme.custom.container)
                    .fillMaxWidth()
                    .height(pv.calculateTopPadding()),
                content = {}
            )
            ScrollableTabRow(
                containerColor = MaterialTheme.custom.container,
                selectedTabIndex = hPagerState.currentPage,
                edgePadding = 0.dp,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[hPagerState.currentPage])
                    )
                }
            ) {
                vm.tabs.forEachIndexed { index, item ->
                    Tab(
                        selected = hPagerState.currentPage == index,
                        selectedContentColor = MaterialTheme.custom.primary,
                        onClick = { coroutineScope.launch { hPagerState.animateScrollToPage(index) } },
                        text = {
                            Text(
                                text = item.name,
                                color = if (hPagerState.currentPage == index) MaterialTheme.custom.primary else MaterialTheme.custom.onContainerPrimary
                            )
                        },
                    )
                }
            }

            HorizontalPager(
                state = hPagerState,
                beyondViewportPageCount = 1,
            ) { _ ->
                PullToRefreshBox(
                    isRefreshing = vm.isRefreshing && data.loadState.isIdle.not(),
                    onRefresh = {
                        vm.setPullRefreshState()
                        data.refresh()
                    },
                    state = pullToRefreshState,
                ) {
                    LazyColumn {
                        if (data.loadState.refresh is LoadState.NotLoading) {
                            vm.setPullRefreshComplete()
                        }
                        items(data.itemCount) { index ->
                            val item = data[index]
                            if (item != null) {
                                key("$index${item.id}") { TopicItem(item) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopicItem(item: Topic) {
    val ctx = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 5.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { TopicDetailsActivity.start(ctx, item.id) }
            .background(MaterialTheme.custom.container)
            .padding(15.dp, 15.dp, 15.dp, 8.dp)
    ) {
        AsyncImage(
            model = item.avatar,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(end = 20.dp)
                .clip(MaterialTheme.shapes.small)
                .size(40.dp)
                .clickable {
                    UserDetailsActivity.start(ctx, item.author)
                }
        )
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.node,
                    textAlign = TextAlign.Left,
                    color = MaterialTheme.custom.onContainerPrimary,
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                    lineHeight = TextUnit(1f, TextUnitType.Sp),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.custom.backgroundSecondary)
                        .padding(3.dp, 1.dp)
                )
                Text(
                    text = item.author,
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                    color = MaterialTheme.custom.onContainerSecondary,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
            Column {
                Text(
                    text = item.title,
                    color = MaterialTheme.custom.onContainerPrimary,
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Row(modifier = Modifier.padding(top = 5.dp)) {
                    Text(
                        text = item.time,
                        fontSize = TextUnit(12f, TextUnitType.Sp),
                        color = MaterialTheme.custom.onContainerSecondary,
                    )
                    item.replies?.let {
                        Text(
                            text = stringResource(R.string.number_of_replies, it.toString()),
                            fontSize = TextUnit(12f, TextUnitType.Sp),
                            color = MaterialTheme.custom.onContainerSecondary,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                    }
                }
            }
        }
    }
}
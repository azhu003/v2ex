@file:OptIn(ExperimentalMaterial3Api::class)

package com.azhu.v2ex.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.azhu.v2ex.ui.component.TopicItem
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
            Spacer(
                modifier = Modifier
                    .background(MaterialTheme.custom.container)
                    .fillMaxWidth()
                    .height(pv.calculateTopPadding()),
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

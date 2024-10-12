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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import coil.compose.AsyncImage
import com.azhu.v2ex.R
import com.azhu.v2ex.data.SubjectItem
import com.azhu.v2ex.ui.activity.SubjectDetailsActivity
import com.azhu.v2ex.ui.activity.UserDetailsActivity
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomePage(vm: HomeViewModel) {
    val pagerState = rememberPagerState { vm.state.value.nodes.size }
    val coroutineScope = rememberCoroutineScope()

    //监听CurrentPage值变化
    LaunchedEffect(pagerState.currentPage) {
        vm.onTabPageSelectChanged(pagerState.currentPage)
    }

    Scaffold { pv ->
        Column {
            // 👇🏻 这个Row组件用来填充系统状态栏使状态栏和TabRow颜色保持一致
            Row(
                modifier = Modifier
                    .background(MaterialTheme.custom.container)
                    .fillMaxWidth()
                    .height(pv.calculateTopPadding())
            ) { }
            ScrollableTabRow(
                containerColor = MaterialTheme.custom.container,
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 0.dp,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                    )
                }
            ) {
                vm.state.value.nodes.forEachIndexed { index, item ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        selectedContentColor = MaterialTheme.custom.primary,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = {
                            Text(
                                text = item.name,
                                color = if (pagerState.currentPage == index) MaterialTheme.custom.primary else MaterialTheme.custom.onContainerPrimary
                            )
                        },
                    )
                }
            }
            HorizontalPager(state = pagerState, beyondViewportPageCount = 1) { page ->
                LazyColumn {
                    itemsIndexed(vm.getSubjectsByTabIndex(page).toList()) { index, item ->
                        key("$index${item.id}") { SubjectItem(item) }
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectItem(item: SubjectItem) {
    val ctx = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 5.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { SubjectDetailsActivity.start(ctx, item.id) }
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
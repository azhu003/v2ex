package com.azhu.v2ex.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.azhu.v2ex.ui.theme.custom
import kotlinx.coroutines.launch

/**
 * @author: Jerry
 * @date: 2024-10-22 15:39
 * @version: 1.0.0
 */
@Composable
fun TabPager(
    modifier: Modifier = Modifier,
    tabArray: Array<String>,
    selectedTabIndex: Int = 0,
    pageContent: @Composable PagerScope.(page: Int) -> Unit
) {
    val tabs = remember { tabArray }
    val pageState = rememberPagerState { tabs.size }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        pageState.scrollToPage(selectedTabIndex)
    }

    Column(modifier) {
        TabRow(
            selectedTabIndex = pageState.currentPage,
            divider = {},
            containerColor = MaterialTheme.custom.container,
            indicator = { positions ->
                if (selectedTabIndex < positions.size) {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(positions[pageState.currentPage])
                    )
                }
            },
        ) {
            tabs.forEachIndexed { index, item ->
                Tab(
                    selected = pageState.currentPage == index,
                    selectedContentColor = MaterialTheme.custom.primary,
                    onClick = { coroutineScope.launch { pageState.animateScrollToPage(index) } },
                    text = {
                        Text(
                            text = item,
                            color = if (pageState.currentPage == index) MaterialTheme.custom.primary else MaterialTheme.custom.onContainerPrimary
                        )
                    },
                )
            }
        }
        HorizontalPager(state = pageState, beyondViewportPageCount = 1, verticalAlignment = Alignment.Top) { index ->
            pageContent(index)
        }
    }
}
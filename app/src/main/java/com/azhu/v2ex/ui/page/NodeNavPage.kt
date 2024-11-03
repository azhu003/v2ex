package com.azhu.v2ex.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azhu.v2ex.R
import com.azhu.v2ex.ui.activity.TopicByNodeActivity
import com.azhu.v2ex.ui.component.LoadingLayout
import com.azhu.v2ex.ui.component.ObserveLifecycleLayout
import com.azhu.v2ex.ui.staggeredgrid.FixedAdaptive
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.viewmodels.NodeNavViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NodeNavPage(vm: NodeNavViewModel) {
    val context = LocalContext.current
    ObserveLifecycleLayout(vm) {
        LoadingLayout(vm.loading, modifier = Modifier.fillMaxSize(), onRetry = vm::fetchData) {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = context.getString(R.string.node_navigation),
                            fontSize = 20.sp
                        )
                    },
                    colors = TopAppBarColors(
                        containerColor = MaterialTheme.custom.container,
                        titleContentColor = MaterialTheme.custom.onContainerPrimary,
                        scrolledContainerColor = Color.Transparent,
                        actionIconContentColor = Color.Transparent,
                        navigationIconContentColor = MaterialTheme.custom.onContainerPrimary,
                    )
                )
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.custom.container)
                        .padding(horizontal = 15.dp)
                ) {
                    vm.navMap.forEach { entry ->
                        Text(
                            text = entry.key,
                            color = MaterialTheme.custom.onContainerPrimary,
                            fontSize = 14.sp,
                            lineHeight = TextUnit(1f, TextUnitType.Sp),
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        LazyHorizontalStaggeredGrid(
                            rows = FixedAdaptive(3, entry.value.size),
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            horizontalItemSpacing = 5.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(
                                    if (entry.value.size < 5) 35.dp
                                    else if (entry.value.size < 10) 60.dp
                                    else 90.dp
                                )
                                .padding(vertical = 5.dp),
                        ) {
                            itemsIndexed(items = entry.value) { index, item ->
                                key(index) {
                                    Box(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .border(
                                                0.2f.dp,
                                                MaterialTheme.custom.onContainerSecondary,
                                                MaterialTheme.shapes.small
                                            )
                                            .clickable { TopicByNodeActivity.start(context, item.key) }
                                            .padding(vertical = 2.dp, horizontal = 8.dp)
                                    ) {
                                        Text(
                                            text = item.label,
                                            fontSize = 14.sp,
                                            lineHeight = 14.sp,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
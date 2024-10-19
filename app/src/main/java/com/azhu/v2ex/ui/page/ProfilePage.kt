package com.azhu.v2ex.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.azhu.basic.provider.AppThemeProvider
import com.azhu.basic.provider.nextTheme
import com.azhu.v2ex.R
import com.azhu.v2ex.ext.startActivityClass
import com.azhu.v2ex.ext.toColor
import com.azhu.v2ex.ui.activity.LoginActivity
import com.azhu.v2ex.ui.component.LoadingLayout
import com.azhu.v2ex.ui.component.ObserveLifecycleLayout
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfilePage(vm: ProfileViewModel) {
    Scaffold { pv ->
        Column(modifier = Modifier.fillMaxSize()) {
            ObserveLifecycleLayout(observer = vm) {
                LoadingLayout(
                    state = vm.state,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (vm.profile.isUnlogged) {
                        Unlogged()
                    } else {
                        UserProfile(vm, pv.calculateTopPadding())
                        UserFeed()
                    }
                }
            }
        }
    }
}

@Composable
fun UserProfile(vm: ProfileViewModel, topPadding: Dp) {
    val context = LocalContext.current
    val profile = vm.profile
    Column(
        modifier = Modifier
            .background(brush = Brush.verticalGradient(listOf("#20364F".toColor(), "#414947".toColor())))
            .padding(start = 15.dp, top = topPadding, end = 15.dp, bottom = 15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
        ) {
            Spacer(Modifier.weight(1f))
            Text(
                text = context.getString(if (profile.isClaimedLoginRewards) R.string.claimed_login_rewards else R.string.claim_login_rewards),
                color = Color.White,
                fontSize = TextUnit(10f, TextUnitType.Sp),
                textAlign = TextAlign.Center,
                lineHeight = TextUnit(1f, TextUnitType.Sp),
                modifier = Modifier
                    .background("#CC4E616C".toColor(), MaterialTheme.shapes.large)
                    .padding(vertical = 2.dp, horizontal = 7.dp)
            )
            Image(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .padding(end = 7.dp)
                    .clickable {
                        val nextTheme = AppThemeProvider.appTheme.nextTheme()
                        AppThemeProvider.onAppThemeChanged(nextTheme)
                    }
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = profile.avatar,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(80.dp)
            )
            Column(modifier = Modifier.padding(start = 15.dp)) {
                Text(
                    text = profile.username,
                    color = Color.White,
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                )
                Text(
                    text = "${context.getString(R.string.account_balance)}: ${profile.balance}",
                    color = "#ACB3B5".toColor(),
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                )
                Text(
                    text = context.getString(R.string.consecutive_login_days, profile.daysOfConsecutiveLogin),
                    color = "#ACB3B5".toColor(),
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                )
            }

        }
        Row(Modifier.padding(top = 15.dp), verticalAlignment = Alignment.CenterVertically) {

            //节点收藏
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${profile.numberOfNodeCollection}",
                    color = Color.White,
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                )
                Text(
                    text = context.getString(R.string.node_collection),
                    color = MaterialTheme.custom.onBackgroundSecondary,
                    lineHeight = TextUnit(1f, TextUnitType.Sp),
                    fontSize = TextUnit(10f, TextUnitType.Sp),
                )
            }
            //主题收藏
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 18.dp)
            ) {
                Text(
                    text = "${profile.numberOfTopicCollection}",
                    color = Color.White,
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                )
                Text(
                    text = context.getString(R.string.node_collection),
                    color = MaterialTheme.custom.onBackgroundSecondary,
                    lineHeight = TextUnit(1f, TextUnitType.Sp),
                    fontSize = TextUnit(10f, TextUnitType.Sp),
                )
            }
            //特别关注
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${profile.numberOfFollowing}",
                    color = Color.White,
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                )
                Text(
                    text = context.getString(R.string.node_collection),
                    color = MaterialTheme.custom.onBackgroundSecondary,
                    lineHeight = TextUnit(1f, TextUnitType.Sp),
                    fontSize = TextUnit(10f, TextUnitType.Sp),
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = context.getString(R.string.edit_profile),
                color = Color.White,
                fontSize = TextUnit(12f, TextUnitType.Sp),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .height(24.dp)
                    .border(0.5f.dp, Color.White, RoundedCornerShape(15.dp))
                    .padding(horizontal = 15.dp)
            )
            Image(
                painter = painterResource(R.drawable.setting),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .height(24.dp)
                    .border(0.3f.dp, Color.White, RoundedCornerShape(15.dp))
                    .padding(vertical = 4.dp, horizontal = 10.dp)
            )
        }
    }
}

@Composable
fun UserFeed() {
    val context = LocalContext.current
    val tabs = remember { context.resources.getStringArray(R.array.user_feed_tabs).toList() }
    val pageState = rememberPagerState { tabs.size }
    val coroutineScope = rememberCoroutineScope()

    TabRow(selectedTabIndex = 0) {
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
    HorizontalPager(
        state = pageState,
        beyondViewportPageCount = 1,
    ) { _ ->
        LazyColumn {
            items(20) {
                key("item$it") {
                    Text(text = "Hello $it", textDecoration = null)
                }
            }
        }
    }
}

@Composable
fun Unlogged() {
    val context = LocalContext.current
    Box(Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Image(painter = painterResource(R.drawable.unlogged), contentDescription = null)
            Text(
                text = context.getString(R.string.unlogged), color = MaterialTheme.custom.onContainerPrimary,
                fontSize = TextUnit(16f, TextUnitType.Sp),
                modifier = Modifier.padding(vertical = 10.dp)
            )
            Button(
                modifier = Modifier
                    .height(36.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    context.startActivityClass(LoginActivity::class)
                }
            ) {
                Text(context.getString(R.string.goto_login), color = Color.White)
            }
        }
    }
}
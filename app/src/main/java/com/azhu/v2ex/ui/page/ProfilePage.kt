package com.azhu.v2ex.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.azhu.basic.provider.AppThemeProvider
import com.azhu.basic.provider.nextTheme
import com.azhu.v2ex.R
import com.azhu.v2ex.ext.startActivityClass
import com.azhu.v2ex.ext.toColor
import com.azhu.v2ex.ui.activity.CollectionActivity
import com.azhu.v2ex.ui.activity.SettingActivity
import com.azhu.v2ex.ui.component.EmptyComponent
import com.azhu.v2ex.ui.component.LoadingLayout
import com.azhu.v2ex.ui.component.ObserveLifecycleLayout
import com.azhu.v2ex.ui.component.RecentlyPublishedTopic
import com.azhu.v2ex.ui.component.RecentlyReply
import com.azhu.v2ex.ui.component.TabPager
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(vm: ProfileViewModel) {
    val pullToRefreshState = rememberPullToRefreshState()
    Scaffold { pv ->
        Column(modifier = Modifier.fillMaxSize()) {
            ObserveLifecycleLayout(observer = vm) {
                LoadingLayout(
                    state = vm.state,
                    modifier = Modifier.fillMaxSize(),
                    onRetry = vm::onRefresh
                ) {
                    if (vm.profile.isUnlogged) {
                        Unlogged(vm)
                    } else {
                        PullToRefreshBox(
                            isRefreshing = vm.isRefreshingByUser,
                            onRefresh = { vm.onRefresh() },
                            state = pullToRefreshState,
                        ) {
                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                UserProfile(vm, pv.calculateTopPadding())
                                UserFeed(vm)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserProfile(vm: ProfileViewModel, topPadding: Dp) {
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
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                lineHeight = TextUnit(1f, TextUnitType.Sp),
                modifier = Modifier
                    .background("#CC4E616C".toColor(), MaterialTheme.shapes.large)
                    .padding(vertical = 2.dp, horizontal = 7.dp)
                    .clickable(vm.isClaimLoginRewardsEnable) { vm.getDayMission() }
            )
            Spacer(Modifier.width(7.dp))
            Image(
                painter = painterResource(if (AppThemeProvider.isDark()) R.drawable.light_mode else R.drawable.dark_mode),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .size(24.dp)
                    .padding(2.dp)
                    .indication(remember { MutableInteractionSource() }, null)
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
                    fontSize = 20.sp,
                )
                Text(
                    text = "${context.getString(R.string.account_balance)}: ${profile.balance}",
                    color = "#ACB3B5".toColor(),
                    fontSize = 12.sp,
                )
                Text(
                    text = context.getString(R.string.consecutive_login_days, profile.daysOfConsecutiveLogin),
                    color = "#ACB3B5".toColor(),
                    fontSize = 12.sp,
                )
            }

        }
        Row(Modifier.padding(top = 15.dp), verticalAlignment = Alignment.CenterVertically) {

            //节点收藏
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { CollectionActivity.start(context, 0) }
            ) {
                Text(
                    text = "${profile.numberOfNodeCollection}",
                    color = Color.White,
                    fontSize = 14.sp,
                )
                Text(
                    text = context.getString(R.string.node_collection),
                    color = MaterialTheme.custom.onBackgroundSecondary,
                    lineHeight = TextUnit(1f, TextUnitType.Sp),
                    fontSize = 10.sp,
                )
            }
            //主题收藏
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .clickable { CollectionActivity.start(context, 1) }
            ) {
                Text(
                    text = "${profile.numberOfTopicCollection}",
                    color = Color.White,
                    fontSize = 14.sp,
                )
                Text(
                    text = context.getString(R.string.topic_collection),
                    color = MaterialTheme.custom.onBackgroundSecondary,
                    lineHeight = TextUnit(1f, TextUnitType.Sp),
                    fontSize = 10.sp,
                )
            }
            //特别关注
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { CollectionActivity.start(context, 2) }
            ) {
                Text(
                    text = "${profile.numberOfFollowing}",
                    color = Color.White,
                    fontSize = 14.sp,
                )
                Text(
                    text = context.getString(R.string.following),
                    color = MaterialTheme.custom.onBackgroundSecondary,
                    lineHeight = TextUnit(1f, TextUnitType.Sp),
                    fontSize = 10.sp,
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = context.getString(R.string.edit_profile),
                color = Color.White,
                fontSize = 12.sp,
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
                    .clickable { context.startActivityClass(SettingActivity::class) }
            )
        }
    }

}

@Composable
private fun UserFeed(vm: ProfileViewModel) {
    val context = LocalContext.current
    val profile = vm.profile

    TabPager(tabArray = context.resources.getStringArray(R.array.user_feed_tabs)) {
        when (it) {
            0 -> {
                if (vm.isTopicEmpty) EmptyComponent() else RecentlyPublishedTopic(
                    topics = profile.topics,
                    isInvisible = profile.topicInvisible,
                    username = profile.username,
                    showFooter = true
                )
            }

            1 -> {
                if (vm.isRepliesEmpty) EmptyComponent() else RecentlyReply(
                    replys = profile.replies,
                    showHead = false,
                    showFooter = true,
                    username = profile.username
                )
            }
        }
    }
}

@Composable
private fun Unlogged(vm: ProfileViewModel) {
    val context = LocalContext.current
    Box(Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Image(painter = painterResource(R.drawable.unlogged), contentDescription = null)
            Text(
                text = context.getString(R.string.unlogged), color = MaterialTheme.custom.onContainerPrimary,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 10.dp)
            )
            Button(
                modifier = Modifier
                    .height(36.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = { vm.toLogin() }
            ) {
                Text(context.getString(R.string.goto_login), color = Color.White)
            }
        }
    }
}
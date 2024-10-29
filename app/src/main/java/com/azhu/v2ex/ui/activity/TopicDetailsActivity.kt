package com.azhu.v2ex.ui.activity

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.azhu.v2ex.R
import com.azhu.v2ex.data.TopicDetails
import com.azhu.v2ex.data.TopicReplyItem
import com.azhu.v2ex.ui.component.ImageWrapper
import com.azhu.v2ex.ui.component.LoadingLayout
import com.azhu.v2ex.ui.component.html.HtmlText
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.viewmodels.TopicDetailsViewModel
import com.king.ultraswiperefresh.UltraSwipeRefresh
import com.king.ultraswiperefresh.indicator.classic.ClassicRefreshFooter
import com.king.ultraswiperefresh.rememberUltraSwipeRefreshState

class TopicDetailsActivity : BaseActivity() {

    companion object {
        fun start(context: Context, tid: String) {
            val intent = Intent(context, TopicDetailsActivity::class.java)
            intent.putExtra("tid", tid)
            context.startActivity(intent)
        }
    }

    private val vm by viewModels<TopicDetailsViewModel>()

    override fun getContentView(): @Composable () -> Unit {
        return { TopicDetailsPage(vm) }
    }

    override fun initialize() {
        super.initialize()
        val sid = intent.getStringExtra("tid")
        if (TextUtils.isEmpty(sid)) {
            finish()
            return
        }
        setAppBarTitle(getString(R.string.topic_details))
        vm.details.value.tid = sid
        vm.fetchTopicDetails()
    }
}

@Composable
private fun TopicDetailsPage(vm: TopicDetailsViewModel) {
    val listState = rememberLazyListState()
    val details = vm.details.value
    val hasMoreData by remember { vm.hasMore }

    val state = rememberUltraSwipeRefreshState()
    LaunchedEffect(vm.isLoadingMoreData.value) {
        state.isLoading = vm.isLoadingMoreData.value
    }

    LoadingLayout(vm.loading, modifier = Modifier.fillMaxSize(), onRetry = vm::fetchTopicDetails) {

        Column {
            UltraSwipeRefresh(
                state = state,
                refreshEnabled = false,
                loadMoreEnabled = hasMoreData,
                onRefresh = {},
                onLoadMore = { vm.fetchTopicDetails(true) },
                footerIndicator = { ClassicRefreshFooter(it) },
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.background(MaterialTheme.custom.container)
                ) {
                    item {
                        TopicBody(details)
                        HorizontalDivider(
                            thickness = 5.dp,
                            color = MaterialTheme.custom.background,
                            modifier = Modifier.padding(top = 15.dp)
                        )
                        Text(
                            text = stringResource(R.string.number_of_replies, details.replyCount ?: "0"),
                            fontSize = TextUnit(15f, TextUnitType.Sp),
                            color = MaterialTheme.custom.onContainerSecondary,
                            modifier = Modifier
                                .padding(horizontal = 15.dp)
                                .padding(vertical = 12.dp)
                        )
                    }
                    itemsIndexed(details.replys.data) { index, item ->
                        key("$index${item.id}") {
                            ReplyItem(vm, item)
                        }
                    }

                    if (details.isInitialized && vm.hasMore.value.not()) {
                        item {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "—·—",
                                    color = MaterialTheme.custom.onContainerSecondary,
                                    fontSize = TextUnit(14f, TextUnitType.Sp),
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
            if (vm.isLogged) {
                HorizontalDivider(Modifier.fillMaxWidth(), Dp.Hairline, color = MaterialTheme.custom.background)
                FooterBar(vm)
            }
        }
    }
}

@Composable
private fun TopicBody(details: TopicDetails) {
    val context = LocalContext.current
    Text(
        text = details.title,
        color = MaterialTheme.custom.onContainerPrimary,
        fontSize = TextUnit(22f, TextUnitType.Sp),
        modifier = Modifier.padding(15.dp)
    )
    Row(
        modifier = Modifier
            .padding(horizontal = 15.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = details.author,
            color = MaterialTheme.custom.primary,
            fontSize = TextUnit(14f, TextUnitType.Sp),
            modifier = Modifier.clickable {
                UserDetailsActivity.start(context, details.author)
            }
        )
        Text(
            text = details.time,
            color = MaterialTheme.custom.onContainerSecondary,
            fontSize = TextUnit(14f, TextUnitType.Sp),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
    HtmlText(
        html = details.content,
        isMarkdown = details.isMarkdown,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 0.dp)
    )
    details.subtitles.forEachIndexed { index, subtitle ->
        Row(
            Modifier
                .padding(top = 15.dp)
                .padding(horizontal = 15.dp)
                .height(IntrinsicSize.Min)
        ) {
            VerticalDivider(thickness = 3.dp, color = MaterialTheme.custom.background)
            Column(Modifier.padding(horizontal = 15.dp)) {
                Text(
                    text = "${context.getString(R.string.subtitle_text, (index + 1))}  ${subtitle.time}",
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    color = MaterialTheme.custom.onContainerPrimary
                )
                HtmlText(
                    html = subtitle.content,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
            }
        }
        if (index < details.subtitles.size - 1) {
            HorizontalDivider(
                modifier = Modifier
                    .padding(top = 15.dp)
                    .padding(start = 30.dp, end = 15.dp),
                thickness = 0.2f.dp,
                color = MaterialTheme.custom.onContainerSecondary
            )
        }
    }
    Text(
        text = context.getString(R.string.number_of_views, details.clicks),
        color = MaterialTheme.custom.onContainerSecondary,
        fontSize = TextUnit(14f, TextUnitType.Sp),
        modifier = Modifier.padding(start = 15.dp, top = 15.dp)
    )
}

@Composable
private fun ReplyItem(vm: TopicDetailsViewModel, item: TopicReplyItem) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        AsyncImage(
            model = item.avatar,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(end = 10.dp)
                .clip(MaterialTheme.shapes.small)
                .size(30.dp)
                .clickable { vm.onViewUserClick(context, item) }
        )
//        Image(
//            painter = painterResource(R.drawable.ic_launcher_foreground),
//            contentDescription = null,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .padding(end = 10.dp)
//                .clip(MaterialTheme.shapes.small)
//                .size(30.dp)
//        )
        Column(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.username,
                    textAlign = TextAlign.Left,
                    color = MaterialTheme.custom.onContainerPrimary,
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    lineHeight = TextUnit(1f, TextUnitType.Sp),
                    modifier = Modifier.clickable { vm.onViewUserClick(context, item) }
                )

                item.badges.forEach {
                    Text(
                        text = it,
                        fontSize = TextUnit(10f, TextUnitType.Sp),
                        color = MaterialTheme.custom.primary,
                        lineHeight = TextUnit(10f, TextUnitType.Sp),
                        modifier = Modifier
                            .padding(start = 3.dp)
                            .border(0.1f.dp, MaterialTheme.custom.primary, MaterialTheme.shapes.extraSmall)
                            .padding(vertical = 0.5.dp, horizontal = 2.dp)
                    )
                }

                Text(
                    text = item.time,
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                    color = MaterialTheme.custom.onContainerSecondary,
                    modifier = Modifier.padding(start = 5.dp)
                )

                if (!item.heart.isNullOrEmpty()) {
                    Text(
                        text = "❤\uFE0F ${item.heart!!}",
                        fontSize = TextUnit(12f, TextUnitType.Sp),
                        color = MaterialTheme.custom.onContainerSecondary,
                        lineHeight = TextUnit(1f, TextUnitType.Sp),
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }

                Spacer(Modifier.weight(1f))
                Text(
                    text = "#${item.no}",
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                    color = MaterialTheme.custom.onContainerSecondary,
                )
            }
            HtmlText(
                html = item.content,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 14f
            )
        }
    }
}

@Composable
private fun FooterBar(vm: TopicDetailsViewModel) {
    val context = LocalContext.current
    val details = vm.details.value
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.custom.container)
            .padding(15.dp),
    ) {
        AsyncImage(
            model = details.avatar,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp)
                .clickable { }
        )
        Text(
            text = details.author, fontSize = TextUnit(14f, TextUnitType.Sp),
            modifier = Modifier
                .padding(start = 6.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(Modifier.weight(1f))
        //thanks
        Column(
            Modifier
                .align(Alignment.CenterVertically)
                .clickable {
                    vm.toast("点击了Tanks按钮")
                }
        ) {
            Text(
                text = "❤\uFE0F",
                fontSize = TextUnit(14f, TextUnitType.Sp),
                color = MaterialTheme.custom.onContainerSecondary,
                lineHeight = TextUnit(1f, TextUnitType.Sp),
                modifier = Modifier.padding(start = 5.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${details.thanks ?: 0}",
                fontSize = TextUnit(14f, TextUnitType.Sp),
                color = MaterialTheme.custom.onContainerSecondary,
                lineHeight = TextUnit(1f, TextUnitType.Sp),
                modifier = Modifier
                    .padding(start = 5.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
        Spacer(Modifier.width(15.dp))
        //Collect
        Column(
            Modifier
                .align(Alignment.CenterVertically)
                .clickable(details.isCollected.value.not()) {
                    vm.toast("点击了关注按钮")
                }
        ) {
            ImageWrapper(
                selected = details.isCollected.value,
                selectedRes = R.drawable.collect_selected,
                unselectedRes = R.drawable.collect_normal,
                contentDescription = context.getString(R.string.collect),
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${details.collections ?: 0}",
                fontSize = TextUnit(14f, TextUnitType.Sp),
                color = MaterialTheme.custom.onContainerSecondary,
                lineHeight = TextUnit(1f, TextUnitType.Sp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        Spacer(Modifier.width(15.dp))
        //Reply
        Column(Modifier.align(Alignment.CenterVertically)) {
            Image(
                painter = painterResource(R.drawable.reply),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${details.replyCount ?: 0}",
                fontSize = TextUnit(14f, TextUnitType.Sp),
                color = MaterialTheme.custom.onContainerSecondary,
                lineHeight = TextUnit(1f, TextUnitType.Sp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
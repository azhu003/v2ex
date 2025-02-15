package com.azhu.v2ex.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azhu.v2ex.R
import com.azhu.v2ex.data.UserDetails
import com.azhu.v2ex.data.UserRecentlyTopic
import com.azhu.v2ex.ui.activity.AllTopicActivity
import com.azhu.v2ex.ui.activity.TopicDetailsActivity
import com.azhu.v2ex.ui.theme.custom

/**
 * @author: Jerry
 * @date: 2024-10-21 20:05
 * @version: 1.0.0
 */
@Composable
fun RecentlyPublishedTopic(details: UserDetails) {
    RecentlyPublishedTopic(details.topics, details.topicInvisible, details.username, true)
}

@Composable
fun RecentlyPublishedTopic(
    topics: SnapshotStateList<UserRecentlyTopic>,
    isInvisible: Boolean,
    username: String,
    showHead: Boolean = false,
    showFooter: Boolean = false
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.custom.container)
    ) {
        if (showHead) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (!isInvisible) AllTopicActivity.start(context, username) }
                    .padding(12.dp)
            ) {
                Text(
                    text = context.getString(R.string.recently_published),
                    color = MaterialTheme.custom.onContainerPrimary,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.custom.onContainerSecondary
                )
            }
        }
        if (isInvisible) {
            //主题列表被隐藏
            Row(
                modifier = Modifier
                    .padding(bottom = 15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.custom.onContainerSecondary
                )
                Text(
                    text = context.getString(R.string.topic_invisible_tips, username),
                    fontSize = 14.sp,
                    color = MaterialTheme.custom.onContainerSecondary,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
        } else {
            val collection = topics.withIndex()
            for ((index, topic) in collection) {
                Column(
                    Modifier
                        .clickable { TopicDetailsActivity.start(context, topic.sid) }
                        .padding(15.dp)
                ) {
                    Text(
                        text = topic.title,
                        color = MaterialTheme.custom.onContainerPrimary,
                        fontSize = 16.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = topic.node.name,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.custom.onContainerPrimary,
                            fontSize = 12.sp,
                            lineHeight = TextUnit(1f, TextUnitType.Sp),
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.custom.backgroundSecondary)
                                .padding(3.dp, 1.dp)
                        )
                        Text(
                            text = topic.time,
                            color = MaterialTheme.custom.onContainerSecondary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                        if (topic.replyCount.isNotBlank()) {
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = context.getString(R.string.number_of_replies, topic.replyCount),
                                color = MaterialTheme.custom.onContainerSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                if (index != collection.count() - 1)
                    HorizontalDivider(thickness = 0.3.dp, modifier = Modifier.padding(horizontal = 15.dp))
            }
            if (showFooter && topics.isNotEmpty()) {
                Text(
                    text = context.getString(R.string.view_more),
                    fontSize = 14.sp,
                    color = MaterialTheme.custom.onContainerSecondary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { AllTopicActivity.start(context, username) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
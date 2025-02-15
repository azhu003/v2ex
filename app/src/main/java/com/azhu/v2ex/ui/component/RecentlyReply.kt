package com.azhu.v2ex.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azhu.v2ex.R
import com.azhu.v2ex.data.UserRecentlyReply
import com.azhu.v2ex.ui.activity.RepliesActivity
import com.azhu.v2ex.ui.activity.TopicDetailsActivity
import com.azhu.v2ex.ui.activity.UserDetailsActivity
import com.azhu.v2ex.ui.component.html.HtmlText
import com.azhu.v2ex.ui.theme.custom

/**
 * @author: Jerry
 * @date: 2024-10-21 20:05
 * @version: 1.0.0
 */
@Composable
fun RecentlyReply(
    replys: List<UserRecentlyReply>,
    showHead: Boolean = true,
    showFooter: Boolean = false,
    username: String? = null
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.custom.container)
    ) {
        if (showHead) {
            RecentlyReplyHeader(username)
        }
        val collection = replys.withIndex()
        for ((index, reply) in collection) {
            RecentlyReplyItem(reply, index == collection.count() - 1)
        }
        if (showFooter && replys.isNotEmpty()) {
            Text(
                text = context.getString(R.string.view_more),
                fontSize = 14.sp,
                color = MaterialTheme.custom.onContainerSecondary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { if (!username.isNullOrEmpty()) RepliesActivity.start(context, username) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun RecentlyReplyHeader(username: String?) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (!username.isNullOrBlank()) {
                    RepliesActivity.start(context, username)
                }
            }
            .padding(12.dp)
    ) {
        Text(
            text = context.getString(R.string.recently_replied),
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

@Composable
fun RecentlyReplyItem(reply: UserRecentlyReply, isLastItem: Boolean) {
    val context = LocalContext.current
    Column(Modifier.padding(12.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.custom.background)
                .padding(10.dp, 8.dp)
        ) {
            Row {
                Text(
                    text = reply.author,
                    color = MaterialTheme.custom.onContainerPrimary,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        UserDetailsActivity.start(context, reply.author)
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = reply.node.name,
                    color = MaterialTheme.custom.onContainerSecondary,
                    fontSize = 14.sp,
                )
            }
            HorizontalDivider(thickness = 0.3.dp)
            Text(
                text = reply.topic,
                color = MaterialTheme.custom.onContainerPrimary,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        TopicDetailsActivity.start(context, reply.sid)
                    }
            )
        }
        HtmlText(
            html = reply.content,
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .padding(8.dp),
            fontSize = 14f
        )
        Row {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = reply.time,
                textDecoration = null,
                fontSize = 12.sp,
                color = MaterialTheme.custom.onContainerSecondary,
                lineHeight = TextUnit(1f, TextUnitType.Sp),
            )
        }
    }
    if (!isLastItem)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp), thickness = 0.3.dp)
}

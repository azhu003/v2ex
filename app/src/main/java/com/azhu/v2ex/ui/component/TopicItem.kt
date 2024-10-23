package com.azhu.v2ex.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.azhu.v2ex.data.Topic
import com.azhu.v2ex.ui.activity.TopicDetailsActivity
import com.azhu.v2ex.ui.activity.UserDetailsActivity
import com.azhu.v2ex.ui.theme.custom

/**
 * @author: Jerry
 * @date: 2024-10-22 22:47
 * @version: 1.0.0
 */

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
                .padding(end = 15.dp)
                .clip(MaterialTheme.shapes.small)
                .size(40.dp)
                .clickable {
                    UserDetailsActivity.start(ctx, item.author)
                }
        )
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (item.node.isNotBlank()) {
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
                    Spacer(Modifier.width(5.dp))
                }
                Text(
                    text = item.author,
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                    color = MaterialTheme.custom.onContainerSecondary,
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
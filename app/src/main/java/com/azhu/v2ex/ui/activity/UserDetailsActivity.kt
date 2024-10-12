package com.azhu.v2ex.ui.activity

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.azhu.basic.provider.logger
import com.azhu.v2ex.R
import com.azhu.v2ex.data.UserDetails
import com.azhu.v2ex.data.UserRecentlyReply
import com.azhu.v2ex.ui.component.html.HtmlText
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.viewmodels.UserDetailsViewModel

class UserDetailsActivity : BaseActivity() {

    companion object {
        fun start(context: Context, username: String) {
            val intent = Intent(context, UserDetailsActivity::class.java)
            intent.putExtra("username", username)
            context.startActivity(intent)
        }
    }

    private val vm by viewModels<UserDetailsViewModel>()

    override fun getContentView(): @Composable () -> Unit {
        return {
            UserDetailsPage(vm.state.value)
        }
    }

    override fun initialize() {
        super.initialize()
        val username = intent.getStringExtra("username") ?: ""
        if (username.isBlank()) finish()
        vm.state.value.username = username
        vm.fetchData()
    }
}

@Composable
private fun UserDetailsPage(details: UserDetails) {
    val scrollState = rememberScrollState()
    Column(Modifier.verticalScroll(scrollState)) {
        UserHeader(details)
        RecentlyPublishedSubject(details)
        RecentlyReply(details.replys)
    }
}

@Composable
private fun UserHeader(details: UserDetails) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .background(MaterialTheme.custom.container)
            .padding(15.dp)
    ) {
        AsyncImage(
            model = details.avatar,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(100.dp)
        )
        Column(
            Modifier
                .padding(start = 15.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = details.username,
                color = MaterialTheme.custom.onContainerPrimary,
                fontSize = TextUnit(22f, TextUnitType.Sp)
            )
            Text(
                text = context.getString(R.string.member_no, details.no),
                color = MaterialTheme.custom.onContainerSecondary,
                fontSize = TextUnit(14f, TextUnitType.Sp),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = context.getString(R.string.register_at, details.registerAt),
                color = MaterialTheme.custom.onContainerSecondary,
                fontSize = TextUnit(14f, TextUnitType.Sp),
            )
            Text(
                text = context.getString(R.string.today_ranking, details.ranking),
                color = MaterialTheme.custom.onContainerSecondary,
                fontSize = TextUnit(14f, TextUnitType.Sp),
            )
        }
    }
}

@Composable
private fun RecentlyPublishedSubject(details: UserDetails) {
    val context = LocalContext.current
    val subjects = details.subjects
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.custom.container)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    logger.info("查看更多")
                }
                .padding(18.dp)
        ) {
            Text(
                text = context.getString(R.string.recently_published),
                color = MaterialTheme.custom.onContainerPrimary,
                fontSize = TextUnit(16f, TextUnitType.Sp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                alignment = Alignment.CenterEnd
            )
        }
        if (details.subjectHidden) {
            //主题列表被隐藏
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(imageVector = Icons.Outlined.Lock, contentDescription = null)
                Text(
                    text = context.getString(R.string.subject_hidden_tips, details.username),
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
        } else {
            val collection = subjects.withIndex()
            for ((index, subject) in collection) {
                Column(
                    Modifier
                        .clickable {
                            SubjectDetailsActivity.start(context, subject.sid)
                        }
                        .padding(15.dp)
                ) {
                    Text(
                        text = subject.title,
                        color = MaterialTheme.custom.onContainerPrimary,
                        fontSize = TextUnit(16f, TextUnitType.Sp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = subject.node.name,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.custom.onContainerPrimary,
                            fontSize = TextUnit(12f, TextUnitType.Sp),
                            lineHeight = TextUnit(1f, TextUnitType.Sp),
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.custom.backgroundSecondary)
                                .padding(3.dp, 1.dp)
                        )
                        Text(
                            text = subject.time,
                            color = MaterialTheme.custom.onContainerSecondary,
                            fontSize = TextUnit(14f, TextUnitType.Sp),
                            modifier = Modifier.padding(start = 5.dp)
                        )
                        if (subject.replyCount.isNotBlank()) {
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = context.getString(R.string.number_of_replies, subject.replyCount),
                                color = MaterialTheme.custom.onContainerSecondary,
                                fontSize = TextUnit(14f, TextUnitType.Sp)
                            )
                        }
                    }
                }
                if (index != collection.count() - 1)
                    HorizontalDivider(thickness = 0.3.dp, modifier = Modifier.padding(horizontal = 15.dp))
            }
        }
    }
}

@Composable
private fun RecentlyReply(replys: List<UserRecentlyReply>) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(5.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.custom.container)
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            Text(
                text = context.getString(R.string.recently_replied),
                color = MaterialTheme.custom.onContainerPrimary,
                fontSize = TextUnit(16f, TextUnitType.Sp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                alignment = Alignment.CenterEnd
            )
        }
        val collection = replys.withIndex()
        for ((index, reply) in collection) {
            Column(Modifier.padding(top = 10.dp)) {
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
                            fontSize = TextUnit(14f, TextUnitType.Sp),
                            modifier = Modifier.clickable {
                                UserDetailsActivity.start(context, reply.author)
                            }
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = reply.node.name,
                            color = MaterialTheme.custom.onContainerSecondary,
                            fontSize = TextUnit(14f, TextUnitType.Sp),
                        )
                    }
                    HorizontalDivider(thickness = 0.3.dp)
                    Text(
                        text = reply.subject,
                        color = MaterialTheme.custom.onContainerPrimary,
                        fontSize = TextUnit(14f, TextUnitType.Sp),
                        modifier = Modifier.clickable {
                            SubjectDetailsActivity.start(context, reply.sid)
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
                        fontSize = TextUnit(12f, TextUnitType.Sp),
                        color = MaterialTheme.custom.onContainerSecondary,
                        lineHeight = TextUnit(1f, TextUnitType.Sp),
                    )
                }
            }
            if (index != collection.count() - 1)
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.3.dp)
        }
    }
}
package com.azhu.v2ex.ui.activity

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.azhu.basic.provider.logger
import com.azhu.v2ex.R
import com.azhu.v2ex.data.UserDetails
import com.azhu.v2ex.ext.toColor
import com.azhu.v2ex.ui.component.RecentlyPublishedTopic
import com.azhu.v2ex.ui.component.RecentlyReply
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
        if (username.isBlank()) {
            finish()
            return
        }
        vm.state.value.username = username
        vm.fetchData()
    }
}

@Composable
private fun UserDetailsPage(details: UserDetails) {
    val scrollState = rememberScrollState()
    Column(Modifier.verticalScroll(scrollState)) {
        UserHeader(details)
        RecentlyPublishedTopic(details)
        RecentlyReply(details.replies, username = details.username)
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
        Column {
            AsyncImage(
                model = details.avatar,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(80.dp)
            )
            if (details.online) {
                Text(
                    text = "ONLINE",
                    lineHeight = TextUnit(1f, TextUnitType.Sp),
                    fontSize = TextUnit(8f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = 7.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(color = "#2AAE67".toColor())
                        .padding(vertical = 1.dp, horizontal = 3.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
        Column(
            Modifier
                .padding(start = 15.dp)
                .fillMaxWidth()
        ) {
            Row {
                Text(
                    text = details.username,
                    color = MaterialTheme.custom.onContainerPrimary,
                    fontSize = TextUnit(22f, TextUnitType.Sp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = if (details.isFollowed) context.getString(R.string.followed) else "+ ${context.getString(R.string.following_of_user)}",
                    fontSize = 10.sp,
                    lineHeight = 10.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 3.dp)
                        .background(
                            MaterialTheme.custom.background,
                            MaterialTheme.shapes.extraSmall
                        )
                        .clickable(details.isFollowed.not()) {
                            logger.i("点击了关注按钮")
                        }
                        .padding(vertical = 2.dp, horizontal = 3.dp)
                )
            }
            Text(
                text = context.getString(R.string.member_no, details.no),
                color = MaterialTheme.custom.onContainerSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = context.getString(R.string.register_at, details.registerAt),
                color = MaterialTheme.custom.onContainerSecondary,
                fontSize = 14.sp,
            )
            if (details.ranking.isNotBlank()) {
                Text(
                    text = context.getString(R.string.today_ranking, details.ranking),
                    color = MaterialTheme.custom.onContainerSecondary,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

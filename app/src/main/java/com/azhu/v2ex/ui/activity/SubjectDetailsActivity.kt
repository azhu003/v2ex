package com.azhu.v2ex.ui.activity

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.azhu.v2ex.R
import com.azhu.v2ex.data.SubjectDetails
import com.azhu.v2ex.data.SubjectReplyItem
import com.azhu.v2ex.ui.component.html.HtmlText
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.viewmodels.SubjectDetailsViewModel

class SubjectDetailsActivity : BaseActivity() {

    companion object {
        fun start(context: Context, subjectId: String) {
            val intent = Intent(context, SubjectDetailsActivity::class.java)
            intent.putExtra("sid", subjectId)
            context.startActivity(intent)
        }
    }

    private val vm by viewModels<SubjectDetailsViewModel>()

    override fun getContentView(): @Composable () -> Unit {
        return { SubjectDetailsPage(vm) }
    }

    override fun initialize() {
        super.initialize()
        val sid = intent.getStringExtra("sid")
        if (TextUtils.isEmpty(sid)) finish()
        vm.state.value.sid = sid
        vm.fetchSubjectDetails()
    }
}

@Composable
private fun SubjectDetailsPage(vm: SubjectDetailsViewModel) {
    val listState = rememberLazyListState()
    val details = vm.state.value
    LazyColumn(
        state = listState,
        modifier = Modifier.background(MaterialTheme.custom.container)
    ) {
        item {
            SubjectBody(details)
            HorizontalDivider(
                thickness = 5.dp,
                color = MaterialTheme.custom.background,
                modifier = Modifier.padding(top = 15.dp)
            )
            Text(
                text = stringResource(R.string.number_of_replies, details.replyCount),
                fontSize = TextUnit(15f, TextUnitType.Sp),
                color = MaterialTheme.custom.onContainerSecondary,
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .padding(top = 15.dp)
            )
        }
        itemsIndexed(details.reply) { index, item ->
            key("$index${item.id}") {
                ReplyItem(item)
            }
        }
    }
}

@Composable
private fun SubjectBody(details: SubjectDetails) {
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
        modifier = Modifier
            .padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 0.dp)
    )
    Text(
        text = context.resources.getString(R.string.number_of_views, details.clicks),
        color = MaterialTheme.custom.onContainerSecondary,
        fontSize = TextUnit(14f, TextUnitType.Sp),
        modifier = Modifier.padding(start = 15.dp, top = 15.dp)
    )
}

@Composable
private fun ReplyItem(item: SubjectReplyItem) {
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
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.username,
                    textAlign = TextAlign.Left,
                    color = MaterialTheme.custom.onContainerPrimary,
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    lineHeight = TextUnit(1f, TextUnitType.Sp),
                )
                Text(
                    text = item.time,
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                    color = MaterialTheme.custom.onContainerSecondary,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
            HtmlText(
                html = item.content,
                modifier = Modifier,
                fontSize = 14f
            )
        }
    }
}
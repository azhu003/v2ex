package com.azhu.v2ex.ui.activity

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.azhu.basic.provider.logger
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
        vm.details.sid = sid
        vm.fetchSubjectDetails()
    }
}

@Composable
private fun SubjectDetailsPage(vm: SubjectDetailsViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    Column(
        Modifier
            .verticalScroll(scrollState)
            .background(MaterialTheme.custom.container)
    ) {
        Text(
            text = vm.details.title.value,
            color = MaterialTheme.custom.onContainerPrimary,
            fontSize = TextUnit(22f, TextUnitType.Sp),
            modifier = Modifier.padding(15.dp)
        )
        Row(
            modifier = Modifier
                .padding(20.dp, 0.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = vm.details.author.value,
                color = MaterialTheme.custom.primary,
                fontSize = TextUnit(14f, TextUnitType.Sp),
                modifier = Modifier.clickable {
                    logger.info("查看用户信息界面")
                    UserDetailsActivity.start(context, vm.details.author.value)
                }
            )
            Text(
                text = vm.details.time.value,
                color = MaterialTheme.custom.onContainerSecondary,
                fontSize = TextUnit(14f, TextUnitType.Sp),
                modifier = Modifier.padding(8.dp, 0.dp)
            )
        }
        HtmlText(
            vm.details.content.value,
            Modifier
                .padding(25.dp, 15.dp)
                .fillMaxWidth()
        )
    }
}
package com.azhu.v2ex.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.viewModelScope
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.HomePageState
import com.azhu.v2ex.data.NodeItem
import com.azhu.v2ex.data.RegexConstant
import com.azhu.v2ex.data.SubjectItem
import com.azhu.v2ex.data.TabPageState
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.startActivityClass
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.ui.activity.SubjectDetailsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import org.jsoup.nodes.Document

/**
 * @description:
 * @author: azhu
 * @date: 2024-09-29 22:23
 * @version: 1.0.0
 */
class HomeViewModel : BaseViewModel() {

    private val state = mutableStateOf(HomePageState())
    private val tabPageState: SnapshotStateMap<Int, TabPageState> = mutableStateMapOf()
    val nodesState = mutableStateListOf<NodeItem>()

    init {
        fetchNodes()
        fetchSubjectsByTabIndex(0, nodesState.first())
    }

    private fun fetchNodes() {
        nodesState.add(NodeItem("recent", "最近"))
        nodesState.add(NodeItem("tach", "技术"))
        nodesState.add(NodeItem("creative", "创意"))
        nodesState.add(NodeItem("play", "好玩"))
        nodesState.add(NodeItem("apple", "Apple"))
        nodesState.add(NodeItem("jobs", "酷工作"))
        nodesState.add(NodeItem("deals", "交易"))
        nodesState.add(NodeItem("city", "城市"))
        nodesState.add(NodeItem("qna", "问与答"))
        nodesState.add(NodeItem("hot", "最热"))
        nodesState.add(NodeItem("all", "全部"))
        nodesState.add(NodeItem("r2", "R2"))
        nodesState.add(NodeItem("xna", "VXNA"))
        nodesState.add(NodeItem("nodes", "节点"))
        nodesState.add(NodeItem("members", "关注"))
    }

    private fun fetchSubjectsByTabIndex(index: Int, node: NodeItem) {
        val subjects = (tabPageState.getOrPut(index) { TabPageState(node) }).subjects
        if (subjects.isEmpty()) {

            http.fetch(onRequestBefore = { logger.debug(">>>> before") }) { http.service.getSubjectList(node.key) }
                .map { Result.success(analysisSubjectDocument(getDocument(it.getOrThrow().byteStream()))) }
                .flowOn(Dispatchers.IO)
                .error { logger.error(it?.message ?: "error message is null") }
                .success { subjects.addAll(it) }
                .launchIn(viewModelScope)

//            getHtmlFromAssets("subjects.html")
//                .map { Result.success(analysisSubjectDocument(getDocument(it.getOrThrow()))) }
//                .flowOn(Dispatchers.IO)
//                .error { logger.error(it?.message ?: "error message is null") }
//                .success { subjects.addAll(it) }
//                .launchIn(viewModelScope)
        }
    }

    private fun analysisSubjectDocument(doc: Document): List<SubjectItem> {
        val subjects = mutableListOf<SubjectItem>()
        val elements = doc.select("div.cell.item tr")
        for (tr in elements) {
            val subject = SubjectItem()
            catch {
                subject.avatar = str { tr.select("img.avatar").attr("src") }
                val topicInfo = tr.children()[2]  // <span class="topic_info">
                val a = topicInfo.select("span.item_title > a")
                subject.id = str { RegexConstant.TOPIC_ID.find(a.attr("href"))!!.value }
                subject.title = str { a.text() }
                subject.node = str { topicInfo.select("a.node").text() }
                subject.operator = str { topicInfo.select("a[href^=/member/]").first()?.text() ?: "" }
                subject.time = str { topicInfo.select("span[title]").text() }
                subject.replies = str { tr.select("a.count_livid").text() }.toIntOrNull()
            }
            subjects.add(subject)
        }
        return subjects
    }

    fun onTabPageSelectChanged(selectedIndex: Int) {
        state.value.index = selectedIndex
        val node = nodesState[selectedIndex]
        fetchSubjectsByTabIndex(selectedIndex, node)
    }

    fun onSubjectItemClick(context: Context, position: Int, item: SubjectItem) {
        context.startActivityClass(SubjectDetailsActivity::class)
    }

    fun getSubjectsByTabIndex(index: Int): SnapshotStateList<SubjectItem> {
        return tabPageState[index]?.subjects ?: mutableStateListOf()
    }

}
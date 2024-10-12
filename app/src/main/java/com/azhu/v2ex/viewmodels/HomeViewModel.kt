package com.azhu.v2ex.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.HomePageState
import com.azhu.v2ex.data.NodeInfo
import com.azhu.v2ex.data.SubjectItem
import com.azhu.v2ex.data.TabPageState
import com.azhu.v2ex.ext.error
import com.azhu.v2ex.ext.smap
import com.azhu.v2ex.ext.success
import com.azhu.v2ex.utils.RegexConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import org.jsoup.nodes.Document

/**
 * @description:
 * @author: azhu
 * @date: 2024-09-29 22:23
 * @version: 1.0.0
 */
class HomeViewModel : BaseViewModel() {

    val state = mutableStateOf(HomePageState())

    init {
        fetchNodes()
        fetchSubjectsByTabIndex(0, state.value.nodes.first())
    }

    private fun fetchNodes() {
        val nodes = state.value.nodes
        nodes.add(NodeInfo("recent", "最近"))
        nodes.add(NodeInfo("tach", "技术"))
        nodes.add(NodeInfo("creative", "创意"))
        nodes.add(NodeInfo("play", "好玩"))
        nodes.add(NodeInfo("apple", "Apple"))
        nodes.add(NodeInfo("jobs", "酷工作"))
        nodes.add(NodeInfo("deals", "交易"))
        nodes.add(NodeInfo("city", "城市"))
        nodes.add(NodeInfo("qna", "问与答"))
        nodes.add(NodeInfo("hot", "最热"))
        nodes.add(NodeInfo("all", "全部"))
        nodes.add(NodeInfo("r2", "R2"))
        nodes.add(NodeInfo("xna", "VXNA"))
        nodes.add(NodeInfo("nodes", "节点"))
        nodes.add(NodeInfo("members", "关注"))
    }

    private fun fetchSubjectsByTabIndex(index: Int, node: NodeInfo) {
        val subjects = (state.value.tabPageMap.getOrPut(index) { TabPageState(node) }).subjects
        if (subjects.isEmpty()) {

            http.fetch { http.service.getSubjectList(node.key) }
                .smap { Result.success(analysisSubjectDocument(getDocument(it.byteStream()))) }
                .flowOn(Dispatchers.IO)
                .error { logger.error("请求错误 ${it?.message}") }
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
            catch {
                val subject = SubjectItem()
                subject.avatar = str { tr.select("img.avatar").attr("src") }
                val topicInfo = tr.children()[2]  // <span class="topic_info">
                val a = topicInfo.select("span.item_title > a")
                subject.id = str { RegexConstant.TOPIC_ID.find(a.attr("href"))!!.value }
                subject.title = str { a.text() }
                subject.node = str { topicInfo.select("a.node").text() }
                subject.author = str { topicInfo.select("a[href^=/member/]").first()?.text() ?: "" }
                subject.time = str { topicInfo.select("span[title]").text() }
                subject.replies = str { tr.select("a.count_livid").text() }.toIntOrNull()
                subjects.add(subject)
            }
        }
        return subjects
    }

    fun onTabPageSelectChanged(selectedIndex: Int) {
        state.value.index = selectedIndex
        val node = state.value.nodes[selectedIndex]
        fetchSubjectsByTabIndex(selectedIndex, node)
    }

    fun getSubjectsByTabIndex(index: Int): SnapshotStateList<SubjectItem> {
        return state.value.tabPageMap[index]?.subjects ?: mutableStateListOf()
    }

}
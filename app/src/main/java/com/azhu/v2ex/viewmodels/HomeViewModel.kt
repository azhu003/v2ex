package com.azhu.v2ex.viewmodels

import androidx.compose.runtime.mutableStateListOf
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
import com.azhu.v2ex.utils.DateTimeUtils
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

    val state = HomePageState()

    init {
        fetchNodes()
        onTabPageSelectChanged(0)
    }

    private fun fetchNodes() {
        val nodes = state.nodes
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

    /**
     * @description: 获取主题列表
     * @author: Jerry
     * @param index 当前Page下标
     * @date: 2024/10/14 00:31
     * @version: v1.0
     */
    private fun fetchSubjectsByTabIndex(index: Int, node: NodeInfo) {
        val subjects = state.tabPageMap[index]?.subjects ?: return
        http.fetch { http.service.getSubjectList(node.key) }
            .smap { Result.success(analysisSubjectDocument(getDocument(it.byteStream()))) }
            .flowOn(Dispatchers.IO)
            .error { logger.error("请求错误 ${it?.message}") }
            .success {
                subjects.clear()
                subjects.addAll(it)
                refreshComplete()
            }
            .launchIn(viewModelScope)

//            getHtmlFromAssets("subjects.html")
//                .map { Result.success(analysisSubjectDocument(getDocument(it.getOrThrow()))) }
//                .flowOn(Dispatchers.IO)
//                .error { logger.error(it?.message ?: "error message is null") }
//                .success { subjects.addAll(it) }
//                .launchIn(viewModelScope)
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
                subject.time = str { DateTimeUtils.ago(topicInfo.select("span[title]").attr("title")) }
                subject.replies = str { tr.select("a.count_livid").text() }.toIntOrNull()
                subjects.add(subject)
            }
        }
        return subjects
    }

    fun onTabPageSelectChanged(selectedIndex: Int) {
        state.index = selectedIndex
        state.tabPageMap.getOrPut(selectedIndex) { TabPageState(state.nodes[selectedIndex]) }
        val tabPage = state.tabPageMap[selectedIndex] ?: return
        if (tabPage.isFirstVisible) {
            fetchSubjectsByTabIndex(selectedIndex, tabPage.node)
            tabPage.isFirstVisible = false
        }
    }

    fun getSubjectsByTabIndex(index: Int): SnapshotStateList<SubjectItem> {
        return state.tabPageMap[index]?.subjects ?: mutableStateListOf()
    }

    fun onRefresh() {
        val index = state.index
        state.tabPageMap[index]!!.isRefreshing.value = true
        state.isRefreshing.value = true
        fetchSubjectsByTabIndex(index, state.nodes[index])
    }

    private fun refreshComplete() {
        val index = state.index
        state.tabPageMap[index]!!.isRefreshing.value = false
        state.isRefreshing.value = false
    }
}
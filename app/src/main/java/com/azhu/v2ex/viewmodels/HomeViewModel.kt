package com.azhu.v2ex.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.viewModelScope
import com.azhu.basic.provider.logger
import com.azhu.v2ex.data.NodeItem
import com.azhu.v2ex.data.SubjectItem
import com.azhu.v2ex.data.TabPageState
import com.azhu.v2ex.ext.startActivityClass
import com.azhu.v2ex.http.ApiService
import com.azhu.v2ex.http.Http
import com.azhu.v2ex.http.Retrofits
import com.azhu.v2ex.http.before
import com.azhu.v2ex.http.error
import com.azhu.v2ex.http.success
import com.azhu.v2ex.ui.activity.SubjectDetailsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import okhttp3.internal.wait
import kotlin.random.Random

/**
 * @description:
 * @author: azhu
 * @date: 2024-09-29 22:23
 * @version: 1.0.0
 */
class HomeViewModel : BaseViewModel() {

    private val tabPageState: SnapshotStateMap<Int, TabPageState> = mutableStateMapOf()
    val nodesState = mutableStateListOf<NodeItem>()

    //fix me. 生成测试用ID
    private val ids = mutableSetOf<Int>()

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

            http.fetch { http.service.getSubjectList("") }
                .before { logger.debug(">>>> before") }
                .flowOn(Dispatchers.IO)
                .success { logger.debug(">>>> success ${it.string()}") }
                .error { logger.debug(">>>> error $it") }
                .launchIn(viewModelScope)

            repeat(40) {
                val rId = getSubjectNextId().toString()
                subjects.add(
                    SubjectItem(
                        id = rId,
                        title = "[$rId] [${node.key}] SteamOS 为什么基于不稳定的 Arch Linux 而不是更成熟、商业化的 Debian？",
                        node = "程序员",
                        operator = "mikewang",
                        time = "3分钟前",
                        replies = Random.nextInt(0, 100)
                    )
                )
            }
        }
    }

    fun onTabPageSelectChanged(selectedIndex: Int) {
        val node = nodesState[selectedIndex]
        fetchSubjectsByTabIndex(selectedIndex, node)
    }

    fun onSubjectItemClick(context: Context, position: Int, item: SubjectItem) {
        context.startActivityClass(SubjectDetailsActivity::class)
    }

    fun getSubjectsByTabIndex(index: Int): SnapshotStateList<SubjectItem> {
        return tabPageState[index]?.subjects ?: mutableStateListOf()
    }

    private fun getSubjectNextId(): Int {
        var next = Random.nextInt(1000, 100000)
        while (ids.contains(next)) {
            next = Random.nextInt(1000, 100000)
        }
        return next
    }
}
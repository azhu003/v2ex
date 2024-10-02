package com.azhu.v2ex.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

@Stable
data class NodeItem(val key: String = "", val name: String = "")

@Stable
data class NodeTabState(
    val index: MutableState<Int>,
    val onTabClick: (Int, NodeItem) -> Unit?
)

@Stable
data class TabPageState(
    val node: NodeItem = NodeItem(),
    val subjects: SnapshotStateList<SubjectItem> = mutableStateListOf(),
)
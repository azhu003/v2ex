package com.azhu.v2ex.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

@Stable
data class NodeItem(val key: String = "", val name: String = "")

@Stable
data class HomePageState(
    var index: Int = 0
)

@Stable
data class TabPageState(
    val node: NodeItem = NodeItem(),
    val subjects: SnapshotStateList<SubjectItem> = mutableStateListOf(),
)
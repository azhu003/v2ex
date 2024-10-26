package com.azhu.v2ex.data

import com.azhu.v2ex.utils.Constant
import org.jsoup.nodes.Document

/**
 * @author: Jerry
 * @date: 2024-10-25 23:53
 * @version: 1.0.0
 */
class NodeNavigationResolver : BaseResolver<Map<String, List<NodeNav>>>() {

    override fun resolver(document: Document): Map<String, List<NodeNav>> {
        val map: MutableMap<String, List<NodeNav>> = mutableMapOf()
        val cellEls = document.select("div#Main > div.box").last()?.select("> div:not(:first-child)") ?: return map
        cellEls.forEach { cell ->
            val parent = cell.select("span.fade").text()
            val nodes = mutableListOf<NodeNav>()
            val aEls = cell.select("a[href^=/go/]")
            aEls.forEach { aEl ->
                val key = Constant.NODE_NAME.find(aEl.attr("href"))?.value
                val label = aEl.text()
                if (!key.isNullOrEmpty() && !label.isNullOrEmpty()) {
                    nodes.add(NodeNav(key, label))
                }
            }
            if (!parent.isNullOrEmpty() && nodes.isNotEmpty()) {
                map[parent] = nodes
            }
        }
        return map
    }
}
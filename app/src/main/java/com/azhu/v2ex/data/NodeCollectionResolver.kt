package com.azhu.v2ex.data

import org.jsoup.nodes.Document

/**
 * @author: Jerry
 * @date: 2024-10-22 21:23
 * @version: 1.0.0
 */
class NodeCollectionResolver : BaseResolver<List<NodeInfo>>() {

    override fun resolver(document: Document): List<NodeInfo> {
        val nodes = mutableListOf<NodeInfo>()
        val array = document.select("div#my-nodes a.fav-node")
        array.forEach { a ->
            val node = NodeInfo()
            node.name = a.select("span.fav-node-name").text()
            node.image = a.select("img[src]").attr("src")
            node.comments = a.select("span.f12.fade").text().trim()
            nodes.add(node)
        }
        return nodes
    }
}
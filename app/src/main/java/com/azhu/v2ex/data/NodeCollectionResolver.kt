package com.azhu.v2ex.data

import com.azhu.v2ex.utils.Constant
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
            node.key = str { Constant.NODE_NAME.find(a.select("a[href^=/go/]").attr("href"))?.value }
            node.name = str { a.select("span.fav-node-name").text() }
            node.image = str { a.select("img[src]").attr("src") }
            node.comments = str { a.select("span.f12.fade").text().trim() }
            nodes.add(node)
        }
        return nodes
    }
}
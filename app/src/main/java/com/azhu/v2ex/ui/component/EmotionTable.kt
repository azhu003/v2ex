package com.azhu.v2ex.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.util.Locale

/**
 * @author: Jerry
 * @date: 2024-11-03 21:27
 * @version: 1.0.0
 */
@Composable
fun EmotionTable(onEmojiClick: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(36.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .fillMaxSize()
            .height(250.dp)
    ) {
        items(emotions.size) {
            val item = emotions[it]
            key(it) {
                AsyncImage(
                    model = item,
                    contentDescription = null,
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            onEmojiClick.invoke(
                                item
                                    .substringAfterLast("/")
                                    .substringBeforeLast(".")
                            )
                        }
                )
            }
        }
    }
}

fun getEmotionUrl(name: String): String? {
    val key = emotionMapping[name] ?: return null
    return String.format(Locale.ROOT, "https://i.imgur.com/%s.png", key)
}

fun getEmotionAssets(name: String): String {
    return String.format(Locale.ROOT, "file:///android_asset/emoticons/%s.png", name)
}

private val emotions by lazy {
    val data = mutableListOf<String>()
    repeat(54) {
        data.add(String.format(Locale.ROOT, "file:///android_asset/emoticons/emotion_%d.png", it + 1))
    }
    return@lazy data
}

private val emotionMapping by lazy {
    mapOf(
        Pair("emotion_1", "MXR7F1M"),
        Pair("emotion_2", "yX7XmjO"),
        Pair("emotion_3", "fnoggUh"),
        Pair("emotion_4", "teR9uGT"),
        Pair("emotion_5", "xVhyt5c"),
        Pair("emotion_6", "yIBiCgx"),
        Pair("emotion_7", "DWCj9zY"),
        Pair("emotion_8", "OVlZeNK"),
        Pair("emotion_9", "CjRq1RY"),
        Pair("emotion_10", "e7WRNLs"),
        Pair("emotion_11", "fORdwpA"),
        Pair("emotion_12", "RFuuu9r"),
        Pair("emotion_13", "HS7UB43"),
        Pair("emotion_14", "DUaHb4y"),
        Pair("emotion_15", "utvKiuP"),
        Pair("emotion_16", "8sDyxEH"),
        Pair("emotion_17", "xDyNxfh"),
        Pair("emotion_18", "xkag28u"),
        Pair("emotion_19", "9FgpEdS"),
        Pair("emotion_20", "0UFEeu1"),
        Pair("emotion_21", "a3UtMXZ"),
        Pair("emotion_22", "RLYytEB"),
        Pair("emotion_23", "CnJKyb6"),
        Pair("emotion_24", "bBcJzaH"),
        Pair("emotion_25", "lWoZ5x2"),
        Pair("emotion_26", "OgmUmYo"),
        Pair("emotion_27", "ptZQq8h"),
        Pair("emotion_28", "tlZgtYY"),
        Pair("emotion_29", "DYrtrCS"),
        Pair("emotion_30", "LQTjZ5z"),
        Pair("emotion_31", "QdeN4CG"),
        Pair("emotion_32", "4z76dZr"),
        Pair("emotion_33", "7Ep8bAM"),
        Pair("emotion_34", "KxrSVrg"),
        Pair("emotion_35", "b6QbOJl"),
        Pair("emotion_36", "xuolly8"),
        Pair("emotion_37", "Oc5Hupf"),
        Pair("emotion_38", "QnqIw6a"),
        Pair("emotion_39", "lvS1rjm"),
        Pair("emotion_40", "ydvkNlf"),
        Pair("emotion_41", "ydvkNlf"),
        Pair("emotion_42", "6lPUHmE"),
        Pair("emotion_43", "6ob0uyC"),
        Pair("emotion_44", "wRAb0BL"),
        Pair("emotion_45", "LhdQT8x"),
        Pair("emotion_46", "xpsC0oV"),
        Pair("emotion_47", "aY4tH5i"),
        Pair("emotion_48", "gRLz9Eb"),
        Pair("emotion_49", "31VJ7PD"),
        Pair("emotion_50", "ubSE9Kt"),
        Pair("emotion_51", "xBg63Zq"),
        Pair("emotion_52", "p23qYiI"),
        Pair("emotion_53", "7C8YtTX"),
        Pair("emotion_54", "k9Pi2YH"),
    )
}

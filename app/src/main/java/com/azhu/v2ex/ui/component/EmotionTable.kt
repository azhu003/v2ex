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
        Pair("emotion_1", "lYWIza4"),
        Pair("emotion_2", "8QdFNRb"),
        Pair("emotion_3", "FW7z33i"),
        Pair("emotion_4", "aUKObRz"),
        Pair("emotion_5", "76CtY5p"),
        Pair("emotion_6", "ml3k6rS"),
        Pair("emotion_7", "uvIjSI2"),
        Pair("emotion_8", "Nm4RvdR"),
        Pair("emotion_9", "rdT64Ur"),
        Pair("emotion_10", "obP7u72"),
        Pair("emotion_11", "nJHRnlT"),
        Pair("emotion_12", "rfSE0WQ"),
        Pair("emotion_13", "uzTB2qK"),
        Pair("emotion_14", "NUivKRx"),
        Pair("emotion_15", "RmGI1qD"),
        Pair("emotion_16", "X6X6L2q"),
        Pair("emotion_17", "JnlUWTs"),
        Pair("emotion_18", "6PnIyrV"),
        Pair("emotion_19", "Zs2X35C"),
        Pair("emotion_20", "0RA4Vso"),
        Pair("emotion_21", "0qIvaFz"),
        Pair("emotion_22", "r3eAzV5"),
        Pair("emotion_23", "NHdS627"),
        Pair("emotion_24", "ZzfEr3K"),
        Pair("emotion_25", "ib73tUV"),
        Pair("emotion_26", "NYbES2S"),
        Pair("emotion_27", "CoSbfs6"),
        Pair("emotion_28", "Sy8vDd2"),
        Pair("emotion_29", "5ZLPrZ3"),
        Pair("emotion_30", "7WATLTl"),
        Pair("emotion_31", "adeu4qS"),
        Pair("emotion_32", "TZNU3a2"),
        Pair("emotion_33", "WRA77mI"),
        Pair("emotion_34", "oymUMNp"),
        Pair("emotion_35", "RbhQyeG"),
        Pair("emotion_36", "F4HBkqr"),
        Pair("emotion_37", "X8X9QxG"),
        Pair("emotion_38", "CkJUWRY"),
        Pair("emotion_39", "DazfXBM"),
        Pair("emotion_40", "DX8JirD"),
        Pair("emotion_41", "MIcihht"),
        Pair("emotion_42", "Ogh8d3O"),
        Pair("emotion_43", "1xDvWMa"),
        Pair("emotion_44", "ynxO0hK"),
        Pair("emotion_45", "SUppCan"),
        Pair("emotion_46", "K8FQR7r"),
        Pair("emotion_47", "foDszCO"),
        Pair("emotion_48", "ugpel9h"),
        Pair("emotion_49", "YlU8zkk"),
        Pair("emotion_50", "lj3JkaH"),
        Pair("emotion_51", "kmQtTUk"),
        Pair("emotion_52", "fWebif1"),
        Pair("emotion_53", "HUIlSAy"),
        Pair("emotion_54", "AUtRVQe"),
    )
}

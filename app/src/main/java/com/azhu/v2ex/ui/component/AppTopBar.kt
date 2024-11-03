package com.azhu.v2ex.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.azhu.v2ex.ui.theme.custom

/**
 * @author: Jerry
 * @date: 2024-11-03 23:34
 * @version: 1.0.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String = "", onBackPress: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                title, color = MaterialTheme.custom.onContainerPrimary,
                fontSize = TextUnit(18f, TextUnitType.Sp)
            )
        },
        colors = TopAppBarColors(
            containerColor = MaterialTheme.custom.container,
            titleContentColor = MaterialTheme.custom.onContainerPrimary,
            scrolledContainerColor = Color.Transparent,
            actionIconContentColor = Color.Transparent,
            navigationIconContentColor = MaterialTheme.custom.onContainerPrimary,
        ),
        navigationIcon = {
            IconButton(onClick = { onBackPress.invoke() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
    )
}
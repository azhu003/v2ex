package com.azhu.v2ex.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azhu.v2ex.R
import com.azhu.v2ex.ui.theme.custom

/**
 * @author: Jerry
 * @date: 2024-10-27 12:22
 * @version: 1.0.0
 */
@Preview(showBackground = true)
@Composable
fun EmptyComponent() {
    val context = LocalContext.current
    val text = remember { context.getString(R.string.no_data) }
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp)
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.no_data),
            contentDescription = text,
            modifier = Modifier.size(56.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = MaterialTheme.custom.onContainerSecondary,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}
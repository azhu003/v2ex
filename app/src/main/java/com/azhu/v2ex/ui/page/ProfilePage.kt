package com.azhu.v2ex.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azhu.basic.provider.AppThemeProvider
import com.azhu.basic.provider.nextTheme

@Composable
fun ProfilePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Greeting()
    }
}

@Preview
@Composable
fun Greeting() {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 14.dp)
    ) {
        Text(text = "当前主题 ${AppThemeProvider.appTheme}", color = MaterialTheme.colorScheme.onBackground)
        Button(
            content = {
                Text("切换主题", color = MaterialTheme.colorScheme.onPrimary)
            },
            onClick = {
                val nextTheme = AppThemeProvider.appTheme.nextTheme()
                AppThemeProvider.onAppThemeChanged(nextTheme)
            }
        )
    }
}
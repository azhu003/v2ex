package com.azhu.v2ex.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.azhu.v2ex.ui.component.LoadingLayout
import com.azhu.v2ex.ui.component.ObserveLifecycleLayout
import com.azhu.v2ex.viewmodels.NodeNavViewModel

@Composable
fun NodeNavPage(vm: NodeNavViewModel) {
    ObserveLifecycleLayout(vm) {
        LoadingLayout(vm.loading, modifier = Modifier.fillMaxSize(), onRetry = vm::fetchData) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Search Screen",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }
        }
    }
}
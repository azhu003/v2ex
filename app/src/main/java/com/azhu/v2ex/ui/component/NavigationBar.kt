package com.azhu.v2ex.ui.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.azhu.v2ex.ui.page.NodeNavPage
import com.azhu.v2ex.ui.page.ProfilePage
import com.azhu.v2ex.ui.page.TabTopicPage
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.viewmodels.NodeNavViewModel
import com.azhu.v2ex.viewmodels.ProfileViewModel
import com.azhu.v2ex.viewmodels.TabTopicViewModel

@Composable
fun NavigationBar(tabTopicVM: TabTopicViewModel, nodeNavViewModel: NodeNavViewModel, profileVM: ProfileViewModel) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.custom.container, modifier = Modifier.height(54.dp)) {
                tabTopicVM.navigations.forEachIndexed { _, item ->
                    NavigationBarItem(
                        colors = NavigationBarItemColors(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary,
                            Color.Transparent,
                            MaterialTheme.custom.onContainerPrimary,
                            MaterialTheme.custom.onContainerPrimary,
                            MaterialTheme.custom.onContainerPrimary,
                            MaterialTheme.custom.onContainerPrimary,
                        ),
                        selected = item.route == currentDestination?.route,
                        label = {
                            Text(
                                text = context.getString(item.label),
                                fontSize = TextUnit(12f, TextUnitType.Sp),
                                lineHeight = TextUnit(12f, TextUnitType.Sp)
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(item.icon),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { pv ->
        NavHost(
            navController = navController,
            startDestination = "topic_page",
            modifier = Modifier.padding(pv)
        ) {
            composable("topic_page") { TabTopicPage(tabTopicVM) }
            composable("node_page") { NodeNavPage(nodeNavViewModel) }
            composable("profile_page") { ProfilePage(profileVM) }
        }
    }
}
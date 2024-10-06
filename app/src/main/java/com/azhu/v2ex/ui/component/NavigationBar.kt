package com.azhu.v2ex.ui.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.azhu.v2ex.ui.main.BottomNavigationItem
import com.azhu.v2ex.ui.main.Routes
import com.azhu.v2ex.ui.page.HomePage
import com.azhu.v2ex.ui.page.ProfilePage
import com.azhu.v2ex.ui.page.SearchPage
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.viewmodels.HomeViewModel

@Composable
fun NavigationBar(homeViewModel: HomeViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.custom.container) {
                BottomNavigationItem().items().forEachIndexed { _, navigationItem ->
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
                        selected = navigationItem.route == currentDestination?.route,
                        label = { Text(navigationItem.label) },
                        icon = { Icon(navigationItem.icon, contentDescription = navigationItem.label) },
                        onClick = {
                            navController.navigate(navigationItem.route) {
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
            startDestination = Routes.Home.route,
            modifier = Modifier.padding(pv)
        ) {
            composable(Routes.Home.route) { HomePage(homeViewModel) }
            composable(Routes.Search.route) { SearchPage() }
            composable(Routes.Profile.route) { ProfilePage() }
        }
    }
}
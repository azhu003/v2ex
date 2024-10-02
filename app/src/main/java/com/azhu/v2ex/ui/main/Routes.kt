package com.azhu.v2ex.ui.main

sealed class Routes(val route : String) {
    data object Home : Routes("home_page")
    data object Search : Routes("search_page")
    data object Profile : Routes("profile_page")
}
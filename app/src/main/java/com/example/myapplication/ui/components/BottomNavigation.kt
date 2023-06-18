package com.example.myapplication.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.FoodList,
        BottomNavItem.Tracker,
        BottomNavItem.MyAccount
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = "") },
                onClick = { navController.navigate(item.screen.route)},
                selected = navBackStackEntry?.destination?.route == item.screen.route
            )
        }
    }
}
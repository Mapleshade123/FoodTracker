package com.example.myapplication.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.List
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material.icons.sharp.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.myapplication.ui.screens.Screen

sealed class BottomNavItem(var title: String, var screen: Screen, var icon:ImageVector){

    object FoodList : BottomNavItem("Food list",Screen.FoodList, Icons.Sharp.List)
    object Tracker: BottomNavItem("Tracker",Screen.MealTracking, Icons.Sharp.ShoppingCart)
    object MyAccount: BottomNavItem("My account",Screen.Account, Icons.Sharp.Person)
}
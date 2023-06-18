package com.example.myapplication.ui.screens

sealed class Screen(val route: String) {
    object FoodList : Screen("foodTracker")
    object AddFood : Screen("addFood")
    data class FoodDetail(val foodId: Int) : Screen("foodDetail/$foodId")
    object MealLog : Screen("mealLog")
    object MealTracking : Screen("mealTracking")
    object Account : Screen("account")
}

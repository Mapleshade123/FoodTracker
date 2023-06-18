package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.AccountScreen
import com.example.myapplication.ui.screens.AddFoodScreen
import com.example.myapplication.ui.screens.FoodDetailScreen
import com.example.myapplication.ui.screens.FoodListScreen
import com.example.myapplication.ui.screens.MealLogScreen
import com.example.myapplication.ui.screens.MealTrackingScreen
import com.example.myapplication.ui.screens.Screen
import com.example.myapplication.viewmodels.FoodViewModel
import com.example.myapplication.viewmodels.FoodViewModelFactory
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: FoodViewModel by viewModels {
        FoodViewModelFactory(AppDatabase.getDatabase(applicationContext))
    }

    private val firebaseApp: FirebaseApp? by lazy { FirebaseApp.initializeApp(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = Screen.FoodList.route) {
                lifecycleScope.launch { }
                composable(Screen.FoodList.route) {
                    FoodListScreen(navController, viewModel)
                }
                composable(Screen.AddFood.route) {
                    AddFoodScreen(navController, viewModel)
                }
                composable(
                    "foodDetail/{foodId}",
                    arguments = listOf(navArgument("foodId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val foodId = backStackEntry.arguments?.getInt("foodId")
                    if (foodId != null) {
                        FoodDetailScreen(navController, viewModel, foodId)
                    }
                }
                composable(Screen.MealLog.route) {
                    MealLogScreen(navController, viewModel)
                }
                composable(Screen.MealTracking.route) {
                    MealTrackingScreen(navController = navController, viewModel = viewModel)
                }
                composable(Screen.Account.route) {
                    println("goncnconcoasdf")
                    firebaseApp?.let {
                        println("let")
                        AccountScreen(navController, viewModel, it)
                    }
                }
            }

        }
    }
}

package com.example.myapplication.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.MealItem
import com.example.myapplication.ui.components.BottomNavigation
import com.example.myapplication.viewmodels.FoodViewModel
import java.time.LocalDateTime
import java.time.ZoneOffset

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealLogScreen(navController: NavController, viewModel: FoodViewModel) {
    var foodName by remember { mutableStateOf("Select food...") }
    var foodQuantity by remember { mutableStateOf("") }

    // fetch all food names from database
    val foods = viewModel.foodItemsLiveData.collectAsState(initial = listOf());

    var expanded by remember { mutableStateOf(false) }
    var foodId = -1

    Scaffold(bottomBar = { BottomNavigation(navController = navController) }, content = {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Log Your Meal", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { expanded = true },
            ) {
                Text(foodName.ifEmpty { "Select Food" })
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "open dropdown")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                foods.value.forEach { food ->
                    DropdownMenuItem(
                        text = { Text(text = food.name) },
                        onClick = {
                            foodName = food.name
                            foodId = food.foodId
                            expanded = false
                        })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = foodQuantity,
                onValueChange = { foodQuantity = it },
                label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (foodId == -1) {
                    return@Button
                }
                val q: Int
                if ( foodQuantity.toIntOrNull() == null ){
                    return@Button
                } else {
                    q = foodQuantity.toInt()
                }

                viewModel.logMeal(
                    MealItem(
                        LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                        q,
                        foodId
                    )
                )
                navController.navigate(Screen.FoodList.route)
            }) {
                Text("Log Meal")
            }
        }
    })
}

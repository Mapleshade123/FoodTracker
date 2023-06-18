package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.FoodItem
import com.example.myapplication.viewmodels.FoodViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(navController: NavController, viewModel: FoodViewModel) {
    val (name, setName) = remember { mutableStateOf("") }
    val (calories, setCalories) = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = setName,
            label = { Text("Food Name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        OutlinedTextField(
            value = calories,
            onValueChange = setCalories,
            label = { Text("Calories") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = {
                if (name.isNotBlank() && calories.isNotBlank()) {
                    // Assume id is auto-incremented in a real database
                    val foodItem = FoodItem(name, calories.toInt())
                    viewModel.addFoodItem(foodItem)
                    navController.popBackStack()
                }
            }
        ) {
            Text(text = "Add Food")
        }
    }
}

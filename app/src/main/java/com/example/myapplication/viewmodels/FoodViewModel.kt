package com.example.myapplication.viewmodels

import com.example.myapplication.AppDatabase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.MealItem
import com.example.myapplication.data.FoodItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset

class FoodViewModel(private val db: AppDatabase) : ViewModel() {
    // A simple in-memory list to store food items
    val foodItemsLiveData = db.foodDao().getAllFood()
    val mealItemsLiveData = db.foodDao().getAllMeals()


    fun addFoodItem(foodItem: FoodItem) {
        viewModelScope.launch {
            db.foodDao().insertAllFood(foodItem)
        }
    }

    fun insertAllFood(vararg foods: FoodItem) {
        viewModelScope.launch {
            db.foodDao().insertAllFood(*foods)
        }
    }

    fun deleteAllFood() {
        viewModelScope.launch {
            db.foodDao().deleteAllFood()
        }
    }

    fun insertAllMeals(vararg meals: MealItem) {
        viewModelScope.launch {
            db.foodDao().insertAllMeals(*meals)
        }
    }

    fun deleteAllMeals() {
        viewModelScope.launch {
            db.foodDao().deleteAllMeals()
        }
    }

    fun logMeal(meal: MealItem) {
        viewModelScope.launch {
            db.foodDao().insertMeal(meal)
        }
    }

    fun getFoodItem(foodId: Int): Flow<FoodItem> {
        return db.foodDao().findById(foodId)
    }

    fun getMealsByDate(from: LocalDateTime, to: LocalDateTime): Flow<List<MealItem>> {
        return db.foodDao().findMealsByDate(from.toEpochSecond(ZoneOffset.UTC), to.toEpochSecond(
            ZoneOffset.UTC))
    }

    fun getCaloriesByDate(from: LocalDateTime, to: LocalDateTime): Flow<Int> {
        return db.foodDao().getCaloriesByDate(from.toEpochSecond(ZoneOffset.UTC), to.toEpochSecond(ZoneOffset.UTC))
    }
}

class FoodViewModelFactory(private val dao: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
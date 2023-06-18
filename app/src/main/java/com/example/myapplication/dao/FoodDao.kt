package com.example.myapplication.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapplication.data.MealItem
import com.example.myapplication.data.FoodItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM fooditem")
    fun getAllFood(): Flow<List<FoodItem>>

    @Query("SELECT * FROM mealitem")
    fun getAllMeals(): Flow<List<MealItem>>

    @Query("SELECT * FROM fooditem WHERE foodId IN (:foodIds)")
    fun loadAllByIds(foodIds: IntArray): Flow<List<FoodItem>>

    @Query("SELECT * FROM fooditem WHERE foodId = (:foodId) LIMIT 1")
    fun findById(foodId: Int): Flow<FoodItem>

    @Insert
    suspend fun insertAllFood(vararg foods: FoodItem)

    @Query("DELETE FROM fooditem")
    suspend fun deleteAllFood()

    @Insert
    suspend fun insertMeal(mealItem: MealItem)

    @Insert
    suspend fun insertAllMeals(vararg meals: MealItem)

    @Query("DELETE FROM mealitem")
    suspend fun deleteAllMeals()

    @Query("SELECT * FROM mealitem WHERE date >= (:from) AND date <= (:to)")
    fun findMealsByDate(from: Long, to: Long): Flow<List<MealItem>>


    @Query(
        "SELECT coalesce(sum(calories * quantity), 0) FROM mealitem " +
                "JOIN fooditem ON mealitem.foodId = fooditem.foodId " +
                "WHERE date >= (:from) AND date <= (:to)"
    )
    fun getCaloriesByDate(from: Long, to: Long): Flow<Int>
}

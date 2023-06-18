package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FoodItem(
    var name: String,
    var calories: Int,
    @PrimaryKey(autoGenerate = true) var foodId: Int = 0,
) {
    constructor() : this("", 0) {
    }
}

@Entity
data class MealItem(
    var date: Long,
    var quantity: Int,
    var foodId: Int,
    @PrimaryKey(autoGenerate = true) var mealId: Int = 0,
){
    constructor() : this(0, 0, 0) {
    }
}

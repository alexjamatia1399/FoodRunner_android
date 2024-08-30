package com.alexander.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity( tableName = "foods")
data class FoodEntity (
        @PrimaryKey val food_id : Int,
        @ColumnInfo(name = "food_name") val name : String,
        @ColumnInfo(name = "food_price") val price : String,
        @ColumnInfo(name = "restaurant_id") val resId : String
)
package com.alexander.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodDao
{
    @Insert
    fun insert (foodEntity: FoodEntity)

    @Delete
    fun delete (foodEntity: FoodEntity)

    @Query("SELECT * FROM foods")
    fun getAllFood() : List<FoodEntity>

    @Query("SELECT * FROM foods WHERE food_id = :foodId")
    fun getFoodById (foodId:String) :FoodEntity
}
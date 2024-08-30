package com.alexander.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao
{
    @Insert
    fun insert (resEntity:RestaurantEntity)

    @Delete
    fun delete (resEntity:RestaurantEntity)

    @Query("SELECT * FROM restaurants")
    fun getAllRes() : List<RestaurantEntity>

    @Query("SELECT * FROM restaurants WHERE res_id = :resId")
    fun getResById (resId:String) :RestaurantEntity

}










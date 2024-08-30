package com.alexander.foodrunner.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FoodEntity::class], version = 1)
abstract class MyCartDatabase : RoomDatabase()
{
    abstract fun foodDao() : FoodDao
}
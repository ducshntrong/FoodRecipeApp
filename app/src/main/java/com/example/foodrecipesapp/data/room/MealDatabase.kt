package com.example.foodrecipesapp.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.foodrecipesapp.data.MealDetail

@Database(entities = [MealDetail::class], version = 1)
abstract class MealDatabase: RoomDatabase() {
    abstract fun mealDao(): MealDao

    companion object{
        @Volatile
        private var INSTANCE:MealDatabase? = null
        fun getInstance(context: Context): MealDatabase{
            val tempInstance = INSTANCE
            if (tempInstance!=null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealDatabase::class.java,
                    "meal_information"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
package com.example.foodrecipesapp.data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.foodrecipesapp.data.MealDetail

@Dao
interface MealDao {
    @Insert
    suspend fun insertFav(meal: MealDetail)

    @Update
    suspend fun updateFav(meal: MealDetail)

    @Delete
    suspend fun deleteFav(meal: MealDetail)

    @Query("SELECT * FROM meal_information where idUser = :idUser order by idMeal asc")
    fun getAllSaveMeals(idUser:String): LiveData<List<MealDetail>>

    @Query("DELETE FROM meal_information WHERE idMeal = :id")
    fun deleteMealById(id:String)

    @Query("SELECT * FROM meal_information WHERE idMeal =:id")
    fun getMealById(id:String):MealDetail
}
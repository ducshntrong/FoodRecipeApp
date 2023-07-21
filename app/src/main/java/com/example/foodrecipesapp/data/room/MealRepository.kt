package com.example.foodrecipesapp.data.room

import androidx.lifecycle.LiveData
import com.example.foodrecipesapp.data.MealDetail

class MealRepository(private val mealDao: MealDao) {
//    val readAllMeal:LiveData<List<MealDetail>> = mealDao.getAllSaveMeals()

    suspend fun insertFav(meal: MealDetail){
        mealDao.insertFav(meal)
    }
    suspend fun updateFav(meal: MealDetail){
        mealDao.updateFav(meal)
    }
    suspend fun deleteFav(meal: MealDetail){
        mealDao.deleteFav(meal)
    }
    suspend fun deleteMealById(mealId: String) {
        mealDao.deleteMealById(mealId)
    }
    suspend fun getMealById(mealId: String): MealDetail {
        return mealDao.getMealById(mealId)
    }

    fun getMealByIdUser(IdUser: String): LiveData<List<MealDetail>> {
        return mealDao.getAllSaveMeals(IdUser)
    }
}
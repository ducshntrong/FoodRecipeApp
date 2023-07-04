package com.example.foodrecipesapp.retrofit

import com.example.foodrecipesapp.data.CategoryResponse
import com.example.foodrecipesapp.data.MealResponse
import com.example.foodrecipesapp.data.RandomMealsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FoodApi {
    @GET("random.php")
    fun getRandomMeal(): Call<RandomMealsResponse>

    @GET("filter.php?")
    fun getMealsByCategory(@Query("c") category:String):Call<MealResponse>

    @GET("categories.php")
    fun getAllCategory(): Call<CategoryResponse>

    @GET("lookup.php?")
    fun getMealById(@Query("i") id:String):Call<RandomMealsResponse>

}
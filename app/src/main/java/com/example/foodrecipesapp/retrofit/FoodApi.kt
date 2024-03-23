package com.example.foodrecipesapp.retrofit

import com.example.foodrecipesapp.data.CategoryResponse
import com.example.foodrecipesapp.data.MealResponse
import com.example.foodrecipesapp.data.RandomMealsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FoodApi {//interface UserAPI sử dụng Retrofit để gọi một API trên server để lấy danh sách data.
//sử dụng annotation @GET để xác định phương thức HTTP được sử dụng để gọi API, và đường dẫn tới API là "random.php".
    @GET("random.php")
    fun getRandomMeal(): Call<RandomMealsResponse>

    @GET("filter.php?")
    fun getMealsByCategory(@Query("c") category:String):Call<MealResponse>

    @GET("categories.php")
    fun getAllCategory(): Call<CategoryResponse>

    @GET("lookup.php?")
    fun getMealById(@Query("i") id:String):Call<RandomMealsResponse>

}
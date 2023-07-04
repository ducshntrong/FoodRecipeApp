package com.example.foodrecipesapp.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foodrecipesapp.data.*
import com.example.foodrecipesapp.retrofit.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel: ViewModel() {
    private val mutableRandomMeal = MutableLiveData<RandomMealsResponse>()
    private val mutableMealsByCategory = MutableLiveData<MealResponse>()
    private val mutableCategory = MutableLiveData<CategoryResponse>()

    init {
        getRandomMeal()
        getMealsByCategory("beef")
        getAllCategory()
    }

    private fun getRandomMeal() {
        Retrofit.foodApi.getRandomMeal().enqueue(object : Callback<RandomMealsResponse> {
            override fun onResponse(call: Call<RandomMealsResponse>, response: Response<RandomMealsResponse>, ) {
                mutableRandomMeal.value = response.body()
            }

            override fun onFailure(call: Call<RandomMealsResponse>, t: Throwable) {
                Log.d("Error", t.message.toString())
            }
        })
    }

    private fun getMealsByCategory(category: String){
        Retrofit.foodApi.getMealsByCategory(category).enqueue(object : Callback<MealResponse>{
            override fun onResponse(call: Call<MealResponse>, response: Response<MealResponse>) {
                mutableMealsByCategory.value = response.body()
            }

            override fun onFailure(call: Call<MealResponse>, t: Throwable) {
                Log.d("Error", t.message.toString())
            }

        })
    }

    private fun getAllCategory(){
        Retrofit.foodApi.getAllCategory().enqueue(object : Callback<CategoryResponse>{
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                mutableCategory.value = response.body()
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                Log.d("Error", t.message.toString())
            }

        })
    }

    fun observeRandomMeal(): LiveData<RandomMealsResponse>{
        return mutableRandomMeal
    }
    fun observeMealsByCategory(): LiveData<MealResponse>{
        return mutableMealsByCategory
    }
    fun observeAllCategory(): LiveData<CategoryResponse>{
        return mutableCategory
    }
}


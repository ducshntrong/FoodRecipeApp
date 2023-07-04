package com.example.foodrecipesapp.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foodrecipesapp.data.Meal
import com.example.foodrecipesapp.data.MealResponse
import com.example.foodrecipesapp.retrofit.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryAcViewModel:ViewModel() {
    private var mutableMeal = MutableLiveData<MealResponse>()
    //hàm lấy meal từ các category
    fun getMealsByCategory(category:String){
        Retrofit.foodApi.getMealsByCategory(category).enqueue(object :
            Callback<MealResponse> {
            override fun onResponse(call: Call<MealResponse>, response: Response<MealResponse>) {
                mutableMeal.value = response.body()
            }

            override fun onFailure(call: Call<MealResponse>, t: Throwable) {
                Log.d("Error",t.message.toString())
            }

        })
    }

    fun observeMeal(): LiveData<MealResponse> {
        return mutableMeal
    }
}
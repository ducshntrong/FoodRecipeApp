package com.example.foodrecipesapp.retrofit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {
    val foodApi:FoodApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")//url cơ sở
            .addConverterFactory(GsonConverterFactory.create())//Converter sẽ lấy tệp json và chuyển đối
            //nó thành 1 đối tượng kotlin
            .build()
            .create(FoodApi::class.java)
    }
}
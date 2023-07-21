package com.example.foodrecipesapp.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.foodrecipesapp.data.*
import com.example.foodrecipesapp.data.room.MealDatabase
import com.example.foodrecipesapp.data.room.MealRepository
import com.example.foodrecipesapp.retrofit.Retrofit
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailModel(application: Application): AndroidViewModel(application) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mutableMealDetail = MutableLiveData<MealDetail>()
    private val mutableMealBottomSheet = MutableLiveData<MealDetail>()

    val readAllMeal: LiveData<List<MealDetail>>
    private val repository: MealRepository
    init {
        val mealDao = MealDatabase.getInstance(application).mealDao()
        repository = MealRepository(mealDao)
        readAllMeal = repository.getMealByIdUser(auth.currentUser?.uid.toString())
    }
    fun getMealByIdUser(IdUser:String){
        viewModelScope.launch(Dispatchers.IO){
            repository.getMealByIdUser(IdUser)
        }
    }
    fun insertFav(meal: MealDetail){
        viewModelScope.launch(Dispatchers.IO){
            repository.insertFav(meal)
        }
    }
    fun updateFav(meal: MealDetail){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateFav(meal)
        }
    }
    fun deleteFav(meal: MealDetail){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteFav(meal)
        }
    }
    fun deleteMealById(mealId:String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMealById(mealId)
        }
    }
    fun getMealById(mealId:String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMealById(mealId)
        }
    }

    //sử dụng để kiểm tra bữa ăn có được lưu trong cơ sở dữ liệu hay không.
    fun isMealSavedInDatabase(mealId: String): Boolean {
        var meal:MealDetail? = null
        //runBlocking để đồng bộ hóa các hoạt động I/O trong khoảng thời gian chạy của hàm
        runBlocking(Dispatchers.IO){
            //sử dụng Dispatchers.IO để thực hiện hoạt động I/O trên một dispatcher khác nhau,
            //để tránh việc block luồng chính (main thread).
            meal = repository.getMealById(mealId)//lấy ra thông tin chi tiết về bữa ăn có mã là mealId và gán cho biến meal
        }
        if (meal==null)
            return false
        return true
    }

    fun getMealDetail(id: String){
        Retrofit.foodApi.getMealById(id).enqueue(object : Callback<RandomMealsResponse>{
            override fun onResponse(
                call: Call<RandomMealsResponse>,
                response: Response<RandomMealsResponse>,
            ) {
                mutableMealDetail.value = response.body()!!.meals[0]
            }

            override fun onFailure(call: Call<RandomMealsResponse>, t: Throwable) {
                Log.d("Error",t.message.toString())
            }
        })
    }
    fun getMealBottomSheet(id: String){
        Retrofit.foodApi.getMealById(id).enqueue(object : Callback<RandomMealsResponse>{
            override fun onResponse(
                call: Call<RandomMealsResponse>,
                response: Response<RandomMealsResponse>,
            ) {
                mutableMealBottomSheet.value = response.body()!!.meals[0]
            }

            override fun onFailure(call: Call<RandomMealsResponse>, t: Throwable) {
                Log.d("Error",t.message.toString())
            }
        })
    }

    fun observeMealDetail(): LiveData<MealDetail> {
        return mutableMealDetail
    }
    fun observeMealBottom(): LiveData<MealDetail> {
        return mutableMealBottomSheet
    }
}
package com.example.foodrecipesapp.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.ViewModel.DetailModel
import com.example.foodrecipesapp.data.MealDB
import com.example.foodrecipesapp.data.MealDetail
import com.example.foodrecipesapp.databinding.ActivityMealDetailesBinding
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.MEAL_ID
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.MEAL_STR
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.MEAL_THUMB
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class MealDetailesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMealDetailesBinding
    private lateinit var detailModel: DetailModel
    private var bundle = Bundle()
    private var mealId = ""
    lateinit var meal: MealDetail
    private var meaToSave: MealDetail?=null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealDetailesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoading()
        auth = FirebaseAuth.getInstance()
        bundle = intent.extras!!
        mealId = bundle.getString(MEAL_ID)!!
        detailModel = ViewModelProvider(this)[DetailModel::class.java]
        setFloatingButtonStatues()

        detailModel.getMealDetail(mealId)
        detailModel.observeMealDetail().observe(this){
            setTextsInViews(it)
            stopLoading()
            meal = it
        }

        binding.imgToolbarBtnBack.setOnClickListener {
            finish()
        }
        onFavoriteClick()
    }

    private fun onFavoriteClick() {
        binding.btnSave.setOnClickListener {
            if (detailModel.isMealSavedInDatabase(mealId)){
                detailModel.deleteMealById(mealId)
                binding.btnSave.setImageResource(R.drawable.ic_baseline_save_24)
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Meal was deleted",
                    Snackbar.LENGTH_SHORT).show()
            }else{
                saveMeal()
                binding.btnSave.setImageResource(R.drawable.ic_saved)
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Meal saved",
                    Snackbar.LENGTH_SHORT).show()
            }
        }
    }
    private fun saveMeal() {
        val meal = MealDetail(meal.idMeal,auth.currentUser?.uid.toString(),meal.strArea,meal.strCategory,
            meal.strInstructions, meal.strMeal,meal.strMealThumb,meal.strYoutube)

        detailModel.insertFav(meal)
    }
    //cập nhật trạng thái của nút lưu trữ bữa ăn (binding.btnSave) trên giao diện người dùng.
    private fun setFloatingButtonStatues() {
        if(detailModel.isMealSavedInDatabase(mealId)){
            binding.btnSave.setImageResource(R.drawable.ic_saved)
        }else{
            binding.btnSave.setImageResource(R.drawable.ic_baseline_save_24)
        }
    }

    private fun setTextsInViews(meal: MealDetail) {
        binding.tvInstructions.text = "- Instructions : "
        binding.tvContent.text = meal.strInstructions
        binding.tvCategoryInfo.text = "Category: "+meal.strCategory
        binding.tvAreaInfo.text = "Area: "+meal.strArea

        binding.collapsingToolbar.title = bundle.getString(MEAL_STR)
        Glide.with(applicationContext)
            .load(bundle.getString(MEAL_THUMB)).into(binding.imgMealDetail)
        binding.imgYoutube.setOnClickListener {

        }
        if (meal.strYoutube!!.isEmpty()){

        }else{
            binding.imgYoutube.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(meal.strYoutube)))
            }
        }
    }
    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSave.visibility = View.GONE
        binding.imgYoutube.visibility = View.INVISIBLE
    }


    private fun stopLoading() {
        binding.progressBar.visibility = View.INVISIBLE
        binding.btnSave.visibility = View.VISIBLE
        binding.imgYoutube.visibility = View.VISIBLE

    }
}
package com.example.foodrecipesapp.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.ViewModel.CategoryAcViewModel
import com.example.foodrecipesapp.ViewModel.DetailModel
import com.example.foodrecipesapp.adapter.MealsAdapter
import com.example.foodrecipesapp.data.Meal
import com.example.foodrecipesapp.data.MealDetail
import com.example.foodrecipesapp.data.MealResponse
import com.example.foodrecipesapp.databinding.ActivityCategoriesBinding
import com.example.foodrecipesapp.databinding.BottomSheetDialogBinding
import com.example.foodrecipesapp.fragments.HomeFragment
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.CATEGORY_NAME
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.CATEGORY_STR
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.CATEGORY_THUMB
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.MEAL_ID
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.MEAL_STR
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.MEAL_THUMB
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.collections.ArrayList

class CategoriesActivity : AppCompatActivity() {
    private var dialog: BottomSheetDialog? = null
    private lateinit var binding: ActivityCategoriesBinding
    lateinit var categoryAcViewModel: CategoryAcViewModel
    lateinit var mealsAdapter: MealsAdapter
    lateinit var detailModel: DetailModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        detailModel = ViewModelProvider(this)[DetailModel::class.java]

        prepareCategory()
        binding.imgToolbarBtnBack.setOnClickListener {
            finish()
        }

        val bundle = intent.extras
        val cateName = bundle?.getString(CATEGORY_NAME)
        categoryAcViewModel = ViewModelProvider(this)[CategoryAcViewModel::class.java]

        Glide.with(this).load(bundle?.getString(CATEGORY_THUMB)).into(binding.imgCategoriesBg)
        Glide.with(this).load(bundle?.getString(CATEGORY_THUMB)).into(binding.imgCategories)
        binding.tvDescCategories.text = bundle?.getString(CATEGORY_STR)
        binding.tvList.text = cateName+" List"

        categoryAcViewModel.getMealsByCategory(cateName!!)
        categoryAcViewModel.observeMeal().observe(this){
            mealsAdapter = MealsAdapter(this,it.meals, object : MealsAdapter.OnClickMeal{
                override fun onClick(position: Int) {
                    onClickMeal(position,it.meals)
                }
            })
            binding.mealRecyclerview.adapter = mealsAdapter
            binding.tvCategoryCount.text = cateName+" : "+it.meals.size.toString()

            searchMeal(it)
            onLongClickMeal()
        }
        showBottomSheet()
    }

    override fun onPause() {
        super.onPause()
        dialog?.dismiss()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val key = if (isLandscape) "image_size_landscape_meal" else "image_size_portrait_meal"
        val imageSize = if (isLandscape) resources.getDimensionPixelSize(R.dimen.image_width_landscape2)
        else resources.getDimensionPixelSize(R.dimen.image_height_portrait_mealCard)
        sharedPreferences.edit().putInt(key, imageSize).apply()
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val newImageWidth:Int
        val newImageHeight:Int
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Xác định kích thước mới của hình ảnh khi xoay ngang
            newImageWidth = resources.getDimensionPixelSize(R.dimen.image_width_landscape2)
            newImageHeight = resources.getDimensionPixelSize(R.dimen.image_height_landscape2)
            // Cập nhật lại kích thước cho ImageView trong ViewHolder
            mealsAdapter.setSize(newImageWidth, newImageHeight)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Xác định kích thước mới của hình ảnh khi xoay dọc
            newImageWidth = resources.getDimensionPixelSize(R.dimen.image_width_portrait_mealCard)
            newImageHeight = resources.getDimensionPixelSize(R.dimen.image_height_portrait_mealCard)
            // Cập nhật lại kích thước cho ImageView trong ViewHolder
            mealsAdapter.setSize(newImageWidth, newImageHeight)
        }

    }

    private fun searchMeal(meal: MealResponse) {
        binding.searchMeal.setOnQueryTextListener(object :OnQueryTextListener,
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filterListMeal(newText, meal.meals)
                }
                return true
            }
        })
    }
    private fun filterListMeal(query: String, list: List<Meal>) {
        if(query!=null){
            var filteredList = ArrayList<Meal>()
            for (i in list){
                if (i.strMeal.contains(query,ignoreCase = true)){
                    filteredList.add(i)
                }
            }
            mealsAdapter = MealsAdapter(this,filteredList, object : MealsAdapter.OnClickMeal{
                override fun onClick(position: Int) {
                    onClickMeal(position,filteredList)
                }
            })
            mealsAdapter.notifyDataSetChanged()
            binding.mealRecyclerview.adapter = mealsAdapter
            onLongClickMeal()
        }
    }
    private fun onLongClickMeal() {
        mealsAdapter.setOnLongClickMeal(object :MealsAdapter.OnLongClickMeal{
            override fun onLongClick(meal: Meal) {
                detailModel.getMealBottomSheet(meal.idMeal)
            }
        })
    }

    private fun showBottomSheet() {
        detailModel.observeMealBottom().observe(this){ meal ->
            dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
            val bottomSheetBinding = BottomSheetDialogBinding.inflate(LayoutInflater.from(this))
            dialog?.setContentView(bottomSheetBinding.root)
            dialog?.show()
            setViewDialogBottomSheet(bottomSheetBinding,meal)
            bottomSheetBinding.bottomSheet.setOnClickListener {
                val i = Intent(applicationContext, MealDetailesActivity::class.java)
                val b = Bundle()
                b.putString(MEAL_ID, meal.idMeal)
                b.putString(MEAL_STR, meal.strMeal)
                b.putString(MEAL_THUMB, meal.strMealThumb)
                i.putExtras(b)
                startActivity(i)
            }
        }
    }
    private fun setViewDialogBottomSheet(
        bottomSheetBinding: BottomSheetDialogBinding,
        meal: MealDetail
    ) {
        Glide.with(this).load(meal.strMealThumb).into(bottomSheetBinding.imgMeal)
        val maxLength = 20
        val limitedDescription = if (meal.strMeal.length > maxLength){
            meal.strMeal.substring(0,maxLength)+"..."
        } else {
            meal.strMeal
        }
        bottomSheetBinding.tvMealNameInBtmsheet.text = limitedDescription

        bottomSheetBinding.tvMealCategory.text = meal.strCategory
        bottomSheetBinding.tvMealCountry.text = meal.strArea
    }


    private fun onClickMeal(position: Int, it: List<Meal>) {
        val i = Intent(applicationContext, MealDetailesActivity::class.java)
        val bundel = Bundle()
        bundel.putString(MEAL_ID, it[position].idMeal)
        bundel.putString(MEAL_STR, it[position].strMeal)
        bundel.putString(MEAL_THUMB, it[position].strMealThumb)
        i.putExtras(bundel)
        startActivity(i)
    }


    private fun prepareCategory() {
        binding.mealRecyclerview.layoutManager = GridLayoutManager(this,
            2,GridLayoutManager.VERTICAL, false)
    }
}
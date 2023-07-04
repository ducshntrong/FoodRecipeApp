package com.example.foodrecipesapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.foodrecipesapp.activity.MealDetailesActivity
import com.example.foodrecipesapp.databinding.BottomSheetDialogBinding
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.CATEGORY_NAME
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.MEAL_AREA
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.MEAL_ID
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.MEAL_NAME
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.MEAL_STR
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.MEAL_THUMB
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MealBottomDialog: BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDialogBinding
    private var mealName = ""
    private var mealId =""
    private var mealImg = ""
    private var mealCountry = ""
    private var mealCategory = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.AppBottomSheetDialogTheme)
        mealName = arguments?.getString(MEAL_NAME).toString()
        mealId =arguments?.getString(MEAL_ID).toString()
        mealImg =arguments?.getString(MEAL_THUMB).toString()
        mealCategory =arguments?.getString(CATEGORY_NAME).toString()
        mealCountry =arguments?.getString(MEAL_AREA).toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView(view)
        view.setOnClickListener {
            val i = Intent(context, MealDetailesActivity::class.java)
            val bundle = Bundle()
            bundle.putString(MEAL_ID,mealId)
            bundle.putString(MEAL_THUMB,mealImg)
            bundle.putString(MEAL_STR,mealName)
            i.putExtras(bundle)
            startActivity(i)
        }
    }

    private fun setView(view: View) {
        Glide.with(view).
            load(mealImg).
            into(binding.imgMeal)

        binding.tvMealCategory.text = mealCategory
        binding.tvMealCountry.text = mealCountry

        val maxLength = 20
        val limitedDescription = if (mealName.length > maxLength){
            mealName.substring(0,maxLength)+"..."
        } else {
            mealName
        }
        binding.tvMealNameInBtmsheet.text = limitedDescription
    }
}
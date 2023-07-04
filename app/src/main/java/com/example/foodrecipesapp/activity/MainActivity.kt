package com.example.foodrecipesapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.databinding.ActivityAddRecipeBinding
import com.example.foodrecipesapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addRecipe.setOnClickListener {
            val i = Intent(this@MainActivity, AddRecipeActivity::class.java)
            startActivity(i)
        }
        val navController = Navigation.findNavController(this, R.id.frag_host)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
    }
}
package com.example.foodrecipesapp.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.foodrecipesapp.BroadcastReceiver.WifiReceiver
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.databinding.ActivityAddRecipeBinding
import com.example.foodrecipesapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var br = WifiReceiver()
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

        var filter = IntentFilter()
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        this.registerReceiver(br,filter)
    }

}
package com.example.foodrecipesapp.activity

import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.foodrecipesapp.BroadcastReceiver.WifiReceiver
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    var br = WifiReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val filter = IntentFilter()
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        this.registerReceiver(br,filter)

        val navController = Navigation.findNavController(this, R.id.frag_host)
        NavigationUI.setupWithNavController(binding.bottomNavigation,navController)
    }
}
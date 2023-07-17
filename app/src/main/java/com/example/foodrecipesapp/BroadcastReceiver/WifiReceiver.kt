package com.example.foodrecipesapp.BroadcastReceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.activity.MainActivity
import com.google.android.material.snackbar.Snackbar

class WifiReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiState = wifiManager.wifiState
        if (wifiState == WifiManager.WIFI_STATE_DISABLED){
            Toast.makeText(context,"You are currently offline!", Toast.LENGTH_LONG).show()
        }
    }
}
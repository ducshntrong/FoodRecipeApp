package com.example.foodrecipesapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foodrecipesapp.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnGetStarted.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            val user = auth.currentUser
            if (user!=null){
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                finish()
            }else{
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
                finish()
            }
        }
    }
}
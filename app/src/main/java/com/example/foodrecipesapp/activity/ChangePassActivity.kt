package com.example.foodrecipesapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.foodrecipesapp.databinding.ActivityChangePassBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class ChangePassActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePassBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.textView5.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
        binding.btnConfirm.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            if (checkEmail()){
                auth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show()
                        val i = Intent(this, LoginActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                }
            }
        }
    }

    private fun checkEmail():Boolean{
        val email = binding.edtEmail.text.toString()
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.edtEmail.error = "Check email format"
            return false
        }
        return true
    }
}
package com.example.foodrecipesapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        binding.textView5.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
        binding.btnSignUp.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val email = binding.edtEmail.text.toString()
        val pass = binding.edtPass.text.toString()
        val rePass = binding.edtPass2.text.toString()
        if (email.isEmpty() || pass.isEmpty() || rePass.isEmpty()){
            Toast.makeText(this, "Please enter full information", Toast.LENGTH_SHORT).show()
        }else{
            if (pass == rePass){
                if (checkEmail()){
                    auth.createUserWithEmailAndPassword(email,pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val i = Intent(this, LoginActivity::class.java)
                                val b = Bundle()
                                b.putString("email",email)
                                b.putString("pass",pass)
                                i.putExtras(b)
                                startActivity(i)
                                Toast.makeText(this, "SignUp succcess", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(
                                    baseContext,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                }
            }else{
                Toast.makeText(this, "Password incorrect", Toast.LENGTH_SHORT).show()
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
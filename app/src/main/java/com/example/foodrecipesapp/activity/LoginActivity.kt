package com.example.foodrecipesapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        val bundle = intent.extras
        binding.edtEmail.setText(bundle?.getString("email"))
        binding.edtPass.setText(bundle?.getString("pass"))

        binding.textView5.setOnClickListener {
            val i = Intent(this, SignupActivity::class.java)
            startActivity(i)
            finish()
        }
        binding.changePass.setOnClickListener {
            val i = Intent(this, ChangePassActivity::class.java)
            startActivity(i)
            finish()
        }
        binding.btnLogin.setOnClickListener {
            loginAccount()
        }
    }

    private fun loginAccount() {
        val email = binding.edtEmail.text.toString()
        val pass = binding.edtPass.text.toString()
        if (email.isEmpty() || pass.isEmpty()){
            Toast.makeText(this, "Please enter full information", Toast.LENGTH_SHORT).show()
        }else{
            auth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val i = Intent(this, MainActivity::class.java)
                        startActivity(i)
                        finish()
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Email or password is incorrect",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }
}
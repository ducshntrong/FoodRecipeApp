package com.example.foodrecipesapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.activity.LoginActivity
import com.example.foodrecipesapp.activity.SplashActivity
import com.example.foodrecipesapp.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        binding.btnLogin.setOnClickListener {
            val i = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(i)
            activity?.finish()
        }
        return binding.root
    }

}
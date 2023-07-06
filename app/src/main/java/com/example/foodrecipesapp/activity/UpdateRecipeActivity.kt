package com.example.foodrecipesapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.data.MealDB
import com.example.foodrecipesapp.databinding.ActivityUpdateRecipeBinding
import com.example.foodrecipesapp.fragments.MyRecipeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class UpdateRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateRecipeBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    lateinit var bundle: Bundle
    lateinit var recipe: MealDB
    private lateinit var storageRef: StorageReference
    private var uri: Uri? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarDetail)
        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        bundle = intent.extras!!
        recipe = bundle.getParcelable("recipe")!!

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().
            getReference("users/${auth.currentUser?.uid}/recipes").child(recipe.idMeal!!)
        storageRef = FirebaseStorage.getInstance().getReference("Images")

        setView(recipe)
        binding.btnUpdate.setOnClickListener {
            updateData(recipe.idMeal!!,binding.edtArea.text.toString(),
                binding.edtInstructions.text.toString(),binding.edtRecipeName.text.toString(),
                binding.edtIngredients.text.toString(),binding.edtYoutube.text.toString())
        }
        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){
            binding.recipeImage.setImageURI(it)
            if (it != null){
                uri = it
            }
        }
        binding.uploadImageBtn.setOnClickListener {
            pickImage.launch("image/*")
        }
        binding.edtIngredients.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }
        binding.edtInstructions.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }
    }

    private fun updateData(idMeal: String, strArea: String,
                           strInstructions: String, strMeal: String,
                           strIngredients: String, strYoutube: String) {
        binding.backg.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
        var meal: MealDB

        if (uri != null){
            storageRef.child(idMeal).putFile(uri!!)
                .addOnSuccessListener{ task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { url ->
                            val imgUri = url.toString()
                            meal = MealDB(idMeal,strArea,strInstructions,strMeal,
                                imgUri,strIngredients,strYoutube)
                            dbRef.setValue(meal)
                                .addOnCompleteListener {
                                    Toast.makeText(this, "Update successfully", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener{ err ->
                                    Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_SHORT).show()
                                }
                            binding.backg.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.VISIBLE

                        }
                }
        }
        if (uri == null){
            meal = MealDB(idMeal,strArea,strInstructions,strMeal,
                recipe.strMealThumb,strIngredients,strYoutube)
            dbRef.setValue(meal)
                .addOnCompleteListener {
                    Toast.makeText(this, "Update successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener{ err ->
                    Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_SHORT).show()
                }
            binding.backg.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }
    }


    private fun setView(recipe: MealDB) {
        binding.edtRecipeName.setText(recipe.strMeal)
        binding.edtArea.setText(recipe.strArea)
        binding.edtYoutube.setText(recipe.strYoutube)
        binding.edtIngredients.setText(recipe.strIngredients)
        binding.edtInstructions.setText(recipe.strInstructions)
        Glide.with(this).load(recipe.strMealThumb).into(binding.recipeImage)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
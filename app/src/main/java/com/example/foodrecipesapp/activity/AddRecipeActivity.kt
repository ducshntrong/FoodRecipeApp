package com.example.foodrecipesapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.foodrecipesapp.data.MealDB
import com.example.foodrecipesapp.databinding.ActivityAddRecipeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream

class AddRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddRecipeBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    var mealImage: String? = ""
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("users/${auth.currentUser?.uid}/recipes")

        binding.btnAdd.setOnClickListener {
            addRecipe()
        }
        binding.uploadImageBtn.setOnClickListener {
            selectImage()
        }

        setSupportActionBar(binding.toolbarDetail)
        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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

    private fun addRecipe() {
        val nameRecipe = binding.edtRecipeName.text.toString()
        val area = binding.edtArea.text.toString()
        val yt = binding.edtYoutube.text.toString()
        val ingredient = binding.edtIngredients.text.toString()
        val instruction = binding.edtInstructions.text.toString()

        val id = dbRef.push().key!!
        val recipe = MealDB(id,area,instruction,nameRecipe,mealImage,ingredient,yt)

        if(nameRecipe.isEmpty() || ingredient.isEmpty()|| instruction.isEmpty()||mealImage!!.isEmpty()){
            Toast.makeText(this, "Please enter full information", Toast.LENGTH_SHORT).show()
        }else{
            dbRef.child(id).setValue(recipe)
                .addOnCompleteListener {
                    Toast.makeText(this, "Data insert successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener{ err ->
                    Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun selectImage(){
        var myFileIntent = Intent(Intent.ACTION_GET_CONTENT)
        myFileIntent.type = "image/*"
        activityResultLauncher.launch(myFileIntent)
    }

    private val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ){result: ActivityResult ->
        if (result.resultCode== RESULT_OK){
            val uri = result.data!!.data
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val myBitmap = BitmapFactory.decodeStream(inputStream)
                val stream = ByteArrayOutputStream()
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)
                val bytes = stream.toByteArray()
                mealImage = Base64.encodeToString(bytes, Base64.DEFAULT)
                binding.recipeImage.setImageBitmap(myBitmap)
                inputStream!!.close()
            }catch (ex:Exception){
                Toast.makeText(this, ex.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
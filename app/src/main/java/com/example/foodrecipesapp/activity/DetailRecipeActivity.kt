package com.example.foodrecipesapp.activity

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.ViewModel.DetailModel
import com.example.foodrecipesapp.data.MealDB
import com.example.foodrecipesapp.data.MealDetail
import com.example.foodrecipesapp.databinding.ActivityDetailRecipeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DetailRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailRecipeBinding
    lateinit var detailModel: DetailModel
    private lateinit var recipe: MealDB
    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        detailModel = ViewModelProvider(this)[DetailModel::class.java]
        setFloatingButtonStatues()

        setSupportActionBar(binding.toolbarDetail)
        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle = intent.extras!!
        recipe = bundle.getParcelable("recipe")!!

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().
        getReference("users/${auth.currentUser?.uid}/recipes").child(recipe.idMeal!!)
        storageRef = FirebaseStorage.getInstance().getReference("Images")

        binding.tvYoutube.setOnClickListener {
            if (recipe.strYoutube!!.isEmpty() || !(recipe.strYoutube!!.contains("youtube.com"))){
                Toast.makeText(this, "No video on Youtube!", Toast.LENGTH_SHORT).show()
            }else{
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(recipe.strYoutube)))
            }
        }

        setTextInView(recipe)
        binding.tvUpdate.setOnClickListener {
            val i = Intent(this, UpdateRecipeActivity::class.java)
            val recipe = MealDB(recipe.idMeal,recipe.strArea,recipe.strInstructions,
                                recipe.strMeal,recipe.strMealThumb,recipe.strIngredients,
                                recipe.strYoutube)
            val bundle = Bundle()
            bundle.putParcelable("recipe",recipe)
            i.putExtras(bundle)
            startActivity(i)
        }
        binding.tvDelete.setOnClickListener {
            deleteMeal()
        }
        onFavoriteClick()
    }

    private fun deleteMeal() {
        val dialog = AlertDialog.Builder(this)
        dialog.apply {
            setTitle("Delete record")
            setMessage("Do you want to delete the recipe?")
            setNegativeButton("No"){ dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Yes"){ dialogInterface: DialogInterface, i: Int ->
                dbRef.removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Delete successfully", Toast.LENGTH_SHORT).show()
                        storageRef.child(recipe.idMeal!!).delete()//xoá img của recipe vừa xoá trong storage
                        finish()
                    }
                    .addOnFailureListener { err ->
                        Toast.makeText(context, err.message, Toast.LENGTH_SHORT).show()
                    }
            }
        }.show()
    }

    override fun onResume() {//cập nhật lại view khi data thay đổi
        super.onResume()
        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val recipe2 = snapshot.getValue(MealDB::class.java)
                if (recipe2 != null) {//ktr nếu dữ liệu có trong db
                    setTextInView(recipe2)
                    val meal = MealDetail(recipe2.idMeal!!,recipe2.strArea!!,recipe2.strIngredients!!,
                        recipe2.strInstructions!!, recipe2.strMeal!!,recipe2.strMealThumb!!,recipe2.strYoutube!!)
                    if(detailModel.isMealSavedInDatabase(recipe2.idMeal!!)){
                        detailModel.updateFav(meal)
                    }
                }else{//nếu data k có trong db thì huỷ activity
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun onFavoriteClick() {
        binding.btnSave.setOnClickListener {
            val bundle = intent.extras!!
            recipe = bundle.getParcelable("recipe")!!
            if (detailModel.isMealSavedInDatabase(recipe.idMeal!!)){
                detailModel.deleteMealById(recipe.idMeal!!)
                binding.btnSave.setImageResource(R.drawable.ic_baseline_save_24)
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Meal was deleted",
                    Snackbar.LENGTH_SHORT).show()
            }else{
                saveMeal()
                binding.btnSave.setImageResource(R.drawable.ic_saved)
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Meal saved",
                    Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveMeal() {
        val bundle = intent.extras!!
        recipe = bundle.getParcelable("recipe")!!
        val recipe = MealDetail(recipe.idMeal!!,recipe.strArea!!,recipe.strIngredients!!,
                        recipe.strInstructions!!, recipe.strMeal!!,recipe.strMealThumb!!,recipe.strYoutube!!)

        detailModel.insertFav(recipe)
    }

    //cập nhật trạng thái của nút lưu trữ bữa ăn (binding.btnSave) trên giao diện người dùng.
    private fun setFloatingButtonStatues() {
        val bundle = intent.extras!!
        recipe = bundle.getParcelable("recipe")!!
        if(detailModel.isMealSavedInDatabase(recipe.idMeal!!)){
            binding.btnSave.setImageResource(R.drawable.ic_saved)
        }else{
            binding.btnSave.setImageResource(R.drawable.ic_baseline_save_24)
        }
    }

    private fun setTextInView(meal: MealDB) {
        this.recipe = meal
        binding.tvTitle.text = meal.strMeal
        binding.tvAreaInfo.text = "Area: "+meal.strArea
        binding.tvIngredients.text = meal.strIngredients
        binding.tvInstructions.text = meal.strInstructions
//        val bytes = Base64.decode(meal.strMealThumb, Base64.DEFAULT)
//        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
//        binding.imgMealDetail.setImageBitmap(bitmap)
        Glide.with(applicationContext).load(meal.strMealThumb).into(binding.imgMealDetail)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
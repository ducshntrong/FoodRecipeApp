package com.example.foodrecipesapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecipesapp.adapter.MyRecipeAdapter
import com.example.foodrecipesapp.data.MealDB
import com.example.foodrecipesapp.databinding.FragmentMyRecipeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyRecipeFragment : Fragment() {
    private lateinit var binding: FragmentMyRecipeBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var ds: ArrayList<MealDB>
    lateinit var myRecipeAdapter: MyRecipeAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMyRecipeBinding.inflate(layoutInflater)
        myRecipeAdapter = MyRecipeAdapter(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("users/${auth.currentUser?.uid}/recipes")

        ds = arrayListOf<MealDB>()
        binding.recipeRecView.setHasFixedSize(true)
        binding.recipeRecView.layoutManager = LinearLayoutManager(requireActivity())
        getDataFromFirebase()

        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val mealID = myRecipeAdapter.getIdByPosition(position)
                val meal = myRecipeAdapter.getMealByPosition(position)
                val dbRef2 = FirebaseDatabase.getInstance().
                    getReference("users/${auth.currentUser?.uid}/recipes").child(mealID)
                dbRef2.removeValue()
                showDeleteSnackBar(mealID,meal)
            }
        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.recipeRecView)

    }

    override fun onResume() {
        super.onResume()
    }

    private fun showDeleteSnackBar(mealId: String, meal: MealDB) {
        Snackbar.make(requireView(), "Meal was deleted", Snackbar.LENGTH_LONG).apply {
            setAction("Undo") {
                dbRef.child(mealId).setValue(meal)
            }.show()
        }
    }

    private fun getDataFromFirebase() {
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                ds.clear()
                if (snapshot.exists()){
                    for (snapRecipe in snapshot.children){
                        val recipe = snapRecipe.getValue(MealDB::class.java)
                        ds.add(recipe!!)
                    }
                    if (ds.isEmpty()){
                        binding.tvFavEmpty.visibility = View.VISIBLE
                    }else{
                        binding.tvFavEmpty.visibility = View.INVISIBLE
                    }
                    myRecipeAdapter.setMealList(ds)
                    binding.recipeRecView.adapter = myRecipeAdapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}
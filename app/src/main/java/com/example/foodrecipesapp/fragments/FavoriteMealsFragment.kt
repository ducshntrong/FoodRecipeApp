package com.example.foodrecipesapp.fragments

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.ViewModel.DetailModel
import com.example.foodrecipesapp.activity.DetailRecipeActivity
import com.example.foodrecipesapp.activity.MealDetailesActivity
import com.example.foodrecipesapp.adapter.MealFavAdapter
import com.example.foodrecipesapp.data.MealDB
import com.example.foodrecipesapp.data.MealDetail
import com.example.foodrecipesapp.databinding.FragmentFavoriteMealsBinding
import com.google.android.material.snackbar.Snackbar

class FavoriteMealsFragment : Fragment() {
    private lateinit var binding : FragmentFavoriteMealsBinding
    lateinit var mealFavAdapter: MealFavAdapter
    private lateinit var detailModel: DetailModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFavoriteMealsBinding.inflate(layoutInflater)
        mealFavAdapter = MealFavAdapter(requireContext())
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
        detailModel = ViewModelProvider(requireActivity())[DetailModel::class.java]

        prepareMealFav()
        detailModel.readAllMeal.observe(viewLifecycleOwner){
            mealFavAdapter.setFavList(it)
            binding.favRecView.adapter = mealFavAdapter
            if(it.isEmpty())
                binding.tvFavEmpty.visibility = View.VISIBLE
            else
                binding.tvFavEmpty.visibility = View.GONE

            binding.searchMeal.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filterListMealFav(newText, it)
                    return true
                }
            })

            mealFavAdapter.setOnItemClick(object : MealFavAdapter.onItemClick{
                override fun onClick(pos: Int) {
                    if (it[pos].strMealThumb.contains("themealdb.com")){
                        sendData(pos, it)
                    }else{
                        sendData2(pos,it)
                    }
                }
            })
        }

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
                val favMeal = mealFavAdapter.getMealByPosition(position)
                detailModel.deleteFav(favMeal)
                showDeleteSnackBar(favMeal)
            }
        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.favRecView)
    }
    override fun onPause() {
        super.onPause()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val key = if (isLandscape) "image_size_landscape_fav" else "image_size_portrait_fav"
        val imageSize = if (isLandscape) resources.getDimensionPixelSize(R.dimen.image_width_landscape2)
        else resources.getDimensionPixelSize(R.dimen.image_height_portrait_fav)
        sharedPreferences.edit().putInt(key, imageSize).apply()
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Xác định kích thước mới của hình ảnh khi xoay ngang
            val newImageWidth = resources.getDimensionPixelSize(R.dimen.image_width_landscape2)
            val newImageHeight = resources.getDimensionPixelSize(R.dimen.image_height_landscape2)
            // Cập nhật lại kích thước cho ImageView trong ViewHolder
            mealFavAdapter.setSize(newImageWidth, newImageHeight)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Xác định kích thước mới của hình ảnh khi xoay dọc
            val newImageWidth = resources.getDimensionPixelSize(R.dimen.image_width_portrait_fav)
            val newImageHeight = resources.getDimensionPixelSize(R.dimen.image_height_portrait_fav)
            // Cập nhật lại kích thước cho ImageView trong ViewHolder
            mealFavAdapter.setSize(newImageWidth, newImageHeight)
        }
    }

    private fun sendData2(pos: Int, it: List<MealDetail>) {
        val i = Intent(requireActivity(), DetailRecipeActivity::class.java)
        val recipe = MealDB(it[pos].idMeal,it[pos].strArea,it[pos].strInstructions,
            it[pos].strMeal,it[pos].strMealThumb,it[pos].strCategory,it[pos].strYoutube)
        val bundle = Bundle()
        bundle.putParcelable("recipe",recipe)
        i.putExtras(bundle)
        startActivity(i)
    }

    private fun showDeleteSnackBar(favMeal: MealDetail) {
        Snackbar.make(requireView(), "Meal was deleted",Snackbar.LENGTH_LONG).apply {
            setAction("Undo") {
                detailModel.insertFav(favMeal)
            }.show()
        }
    }

    private fun sendData(pos: Int, it: List<MealDetail>) {
        val i = Intent(requireActivity(), MealDetailesActivity::class.java)
        val bundel = Bundle()
        bundel.putString(HomeFragment.MEAL_ID, it[pos].idMeal)
        bundel.putString(HomeFragment.MEAL_STR, it[pos].strMeal)
        bundel.putString(HomeFragment.MEAL_THUMB, it[pos].strMealThumb)
        i.putExtras(bundel)
        startActivity(i)
    }

    private fun filterListMealFav(query: String?, list: List<MealDetail>) {
        if(query!=null){
            var filteredList = ArrayList<MealDetail>()
            for (i in list){
                if (i.strMeal!!.contains(query,ignoreCase = true)){
                    filteredList.add(i)
                }
            }
            mealFavAdapter.setFavList(filteredList)
            binding.favRecView.adapter = mealFavAdapter
            mealFavAdapter.setOnItemClick(object : MealFavAdapter.onItemClick{
                override fun onClick(pos: Int) {
                    if (filteredList[pos].strMealThumb.contains("themealdb.com")){
                        sendData(pos, filteredList)
                    }else{
                        sendData2(pos, filteredList)
                    }
                }
            })
        }
    }

    private fun prepareMealFav() {
        binding.favRecView.layoutManager = GridLayoutManager(context, 2,
            GridLayoutManager.VERTICAL, false)
    }
}
package com.example.foodrecipesapp.fragments

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.foodrecipesapp.*
import com.example.foodrecipesapp.ViewModel.DetailModel
import com.example.foodrecipesapp.ViewModel.HomeViewModel
import com.example.foodrecipesapp.activity.CategoriesActivity
import com.example.foodrecipesapp.activity.LoginActivity
import com.example.foodrecipesapp.activity.MealDetailesActivity
import com.example.foodrecipesapp.adapter.CategoryAdapter
import com.example.foodrecipesapp.adapter.PopularAdapter
import com.example.foodrecipesapp.data.*
import com.example.foodrecipesapp.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    private var bottomSheetFragment: MealBottomDialog?= null
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    lateinit var homeViewModel: HomeViewModel
    private var randomMealId = ""
    private lateinit var meal: RandomMealsResponse
    private lateinit var popularAdapter: PopularAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    lateinit var detailModel: DetailModel

    companion object {
        const val MEAL_ID = "com.example.foodrecipesapp.idMeal"
        const val MEAL_NAME = "com.example.foodrecipesapp.nameMeal"
        const val MEAL_THUMB = "com.example.foodrecipesapp.thumbMeal"
        const val CATEGORY_NAME = "com.example.foodrecipesapp.categoryName"
        const val CATEGORY_STR = "com.example.foodrecipesapp.categoryStr"
        const val CATEGORY_THUMB = "com.example.foodrecipesapp.categoryThumb"
        const val MEAL_AREA = "com.example.foodrecipesapp.areaMeal"
        const val MEAL_STR = "com.example.foodrecipesapp.strMeal"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailModel = ViewModelProvider(requireActivity())[DetailModel::class.java]
        binding = FragmentHomeBinding.inflate(layoutInflater)
        popularAdapter = PopularAdapter()
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
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        showLoadingCase()
        auth = FirebaseAuth.getInstance()

        preparePopularMeals()
        prepareCategorys()
        onRandomMealClick()
        onRandomMealLongClick()

        binding.imgLogout.setOnClickListener {
            auth.signOut()
            val i = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(i)
            activity?.finish()
        }

        homeViewModel.observeRandomMeal().observe(viewLifecycleOwner) {
            val imageUrl = it!!.meals[0].strMealThumb//!! giá trị not null
            randomMealId = it.meals[0].idMeal
            //sd Glide để lấy ảnh từ internet và đặt nó vào img_random_meal
            Glide.with(requireActivity())
                .load(imageUrl)
                .into(binding.imgRandomMeal)
            meal = it
        }

        homeViewModel.observeMealsByCategory().observe(viewLifecycleOwner) {
            popularAdapter.setMealList(it.meals)
            binding.recViewMealsPopular.adapter = popularAdapter
            popularAdapter.setOnPopuClick(object: PopularAdapter.OnClickPopular{
                override fun onItemClick(pos: Int) {
                    sendDataToMealDetail(pos, it.meals)
                }
            })
            cancelLoadingCase()

            searchMeal(it)
        }

        popularAdapter.setOnLongPopuClick(object :PopularAdapter.OnLongClickPopular{
            override fun onLongItemClick(meal: Meal) {
                detailModel.getMealBottomSheet(meal.idMeal)
            }
        })

        detailModel.observeMealBottom().observe(viewLifecycleOwner){
            bottomSheetFragment = MealBottomDialog()
            val b = Bundle()
            b.putString(CATEGORY_NAME,it.strCategory)
            b.putString(MEAL_AREA,it.strArea)
            b.putString(MEAL_NAME,it.strMeal)
            b.putString(MEAL_THUMB,it.strMealThumb)
            b.putString(MEAL_ID,it.idMeal)
            bottomSheetFragment?.arguments = b
            bottomSheetFragment?.show(childFragmentManager,"BottomSheetDialog")
        }

//        binding.imgSearch.setOnClickListener {
//            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
//        }

        homeViewModel.observeAllCategory().observe(viewLifecycleOwner){
            categoryAdapter = CategoryAdapter(it.categories, object: CategoryAdapter.OnClickCategory{
                override fun onClickCate(pos: Int) {
                    val i = Intent(requireContext(), CategoriesActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString(CATEGORY_NAME, it.categories[pos].strCategory)
                    bundle.putString(CATEGORY_STR, it.categories[pos].strCategoryDescription)
                    bundle.putString(CATEGORY_THUMB, it.categories[pos].strCategoryThumb)
                    i.putExtras(bundle)
                    startActivity(i)
                }
            })
            binding.recyclerView.adapter = categoryAdapter
        }

    }

    override fun onPause() {
        super.onPause()
       bottomSheetFragment?.dismiss()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Xác định kích thước mới của hình ảnh khi xoay ngang
            val newImageWidth = resources.getDimensionPixelSize(R.dimen.image_width_landscape)
            val newImageHeight = resources.getDimensionPixelSize(R.dimen.image_height_landscape)
            // Cập nhật lại kích thước cho ImageView trong ViewHolder
            categoryAdapter.setSize(newImageWidth, newImageHeight)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Xác định kích thước mới của hình ảnh khi xoay dọc
            val newImageWidth = resources.getDimensionPixelSize(R.dimen.image_width_portrait_cate)
            val newImageHeight = resources.getDimensionPixelSize(R.dimen.image_height_portrait_cate)
            // Cập nhật lại kích thước cho ImageView trong ViewHolder
            categoryAdapter.setSize(newImageWidth, newImageHeight)
        }
    }

    private fun searchMeal(meal: MealResponse) {
        binding.search.setOnQueryTextListener(object :OnQueryTextListener,
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filterListMeal(newText, meal.meals)
                }
                return true
            }

        })
    }
    private fun filterListMeal(query: String, list: List<Meal>) {
        val filteredList = ArrayList<Meal>()
        for (i in list){
            if (i.strMeal.contains(query,ignoreCase = true)){
                filteredList.add(i)
            }
        }
        popularAdapter.setMealList(filteredList)
        binding.recViewMealsPopular.adapter = popularAdapter
        popularAdapter.setOnPopuClick(object: PopularAdapter.OnClickPopular{
            override fun onItemClick(pos: Int) {
                sendDataToMealDetail(pos, filteredList)
            }
        })
    }

    private fun sendDataToMealDetail(pos: Int, list: List<Meal>) {
        val i = Intent(requireContext(), MealDetailesActivity::class.java)
        val bundel = Bundle()
        bundel.putString(MEAL_ID, list[pos].idMeal)
        bundel.putString(MEAL_STR, list[pos].strMeal)
        bundel.putString(MEAL_THUMB, list[pos].strMealThumb)
        i.putExtras(bundel)
        startActivity(i)
    }

    private fun onRandomMealLongClick() {
        binding.randomMeal.setOnLongClickListener {
            detailModel.getMealBottomSheet(randomMealId)
            true
        }
    }

    private fun onRandomMealClick() {
        binding.randomMeal.setOnClickListener {
        val temp = meal.meals[0]
            val i  = Intent(requireContext(), MealDetailesActivity::class.java)
            val bundel = Bundle()
            bundel.putString(MEAL_ID, temp.idMeal)
            bundel.putString(MEAL_STR, temp.strMeal)
            bundel.putString(MEAL_THUMB, temp.strMealThumb)
            i.putExtras(bundel)
            startActivity(i)
        }
    }

    private fun prepareCategorys() {
        binding.recyclerView.layoutManager = GridLayoutManager(context,2,
            GridLayoutManager.VERTICAL,false)
    }

    private fun preparePopularMeals() {
        binding.recViewMealsPopular.layoutManager = GridLayoutManager(context,1,
            GridLayoutManager.HORIZONTAL,false)
    }
    private fun showLoadingCase() {
        binding.apply {
            header.visibility = View.INVISIBLE
            tvWouldLikeToEat.visibility = View.INVISIBLE
            randomMeal.visibility = View.INVISIBLE
            tvOverPupItems.visibility = View.INVISIBLE
            categoryCard2.visibility = View.INVISIBLE
            tvCategory.visibility = View.INVISIBLE
            categoryCard.visibility = View.INVISIBLE
            loadingGif.visibility = View.VISIBLE
//            rootHome.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.g_loading))
        }
    }

    private fun cancelLoadingCase() {
        binding.apply {
            header.visibility = View.VISIBLE
            tvWouldLikeToEat.visibility = View.VISIBLE
            randomMeal.visibility = View.VISIBLE
            tvOverPupItems.visibility = View.VISIBLE
            categoryCard2.visibility = View.VISIBLE
            tvCategory.visibility = View.VISIBLE
            categoryCard.visibility = View.VISIBLE
            loadingGif.visibility = View.INVISIBLE
//            rootHome.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

        }
    }
}
package com.example.foodrecipesapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foodrecipesapp.activity.CategoriesActivity
import com.example.foodrecipesapp.ViewModel.HomeViewModel
import com.example.foodrecipesapp.adapter.CategoryAdapter
import com.example.foodrecipesapp.adapter.CategoryAdapter2
import com.example.foodrecipesapp.data.Category
import com.example.foodrecipesapp.databinding.FragmentCategoryBinding
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.CATEGORY_STR
import com.example.foodrecipesapp.fragments.HomeFragment.Companion.CATEGORY_THUMB
import kotlin.collections.ArrayList

class CategoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding
    lateinit var cateViewModel: HomeViewModel
    lateinit var categoryAdapter: CategoryAdapter2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cateViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        binding.favoriteRecyclerView.layoutManager = GridLayoutManager(context, 2,
            GridLayoutManager.VERTICAL, false)
        cateViewModel.observeAllCategory().observe(viewLifecycleOwner){
            categoryAdapter = CategoryAdapter2(it.categories, object: CategoryAdapter2.OnClickCategory{
                override fun onClickCate(pos: Int) {
                    onClickCategory(pos,it.categories)
                }
            })
            binding.favoriteRecyclerView.adapter = categoryAdapter

            binding.searchCate.setOnQueryTextListener(object: OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filterListCate(newText, it.categories)
                    return true
                }
            })
        }

    }

    private fun onClickCategory(pos: Int, categories: List<Category>) {
        val i = Intent(requireContext(), CategoriesActivity::class.java)
        val bundle = Bundle()
        bundle.putString(HomeFragment.CATEGORY_NAME, categories[pos].strCategory)
        bundle.putString(CATEGORY_STR, categories[pos].strCategoryDescription)
        bundle.putString(CATEGORY_THUMB, categories[pos].strCategoryThumb)
        i.putExtras(bundle)
        startActivity(i)
    }

    private fun filterListCate(query: String?, categories: List<Category>) {
        if (query != null){
            var filteredList = ArrayList<Category>()
            for (i in categories){
                if (i.strCategory.contains(query,ignoreCase = true)){
                    filteredList.add(i)
                }
            }
            categoryAdapter = CategoryAdapter2(filteredList, object: CategoryAdapter2.OnClickCategory{
                override fun onClickCate(pos: Int) {
                    onClickCategory(pos,filteredList)
                }
            })
            categoryAdapter.notifyDataSetChanged()
            binding.favoriteRecyclerView.adapter = categoryAdapter
        }
    }
}
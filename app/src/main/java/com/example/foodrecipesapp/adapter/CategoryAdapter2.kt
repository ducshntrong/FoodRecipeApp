package com.example.foodrecipesapp.adapter

import android.content.Context
import android.content.res.Configuration
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.data.Category

class CategoryAdapter2(context: Context,private var listCate: List<Category>, val onClickItem: OnClickCategory):
    RecyclerView.Adapter<CategoryAdapter2.CategoryViewHolder>() {
    private var imageWidth: Int = 440
    private var imageHeight: Int = 358
    init {
        // Lấy kích thước hiện tại của màn hình từ SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val isLandscape = context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val key = if (isLandscape) "image_size_landscape_cate2" else "image_size_portrait_cate2"
        val defaultValue = if (isLandscape) 960 else 440
        val imageSize = sharedPreferences.getInt(key, defaultValue)
        if (isLandscape){
            imageWidth = imageSize
            imageHeight = imageSize
        }else{
            imageWidth = 440
            imageHeight = 358
        }
    }
    fun setSize(width: Int, height: Int){
        imageWidth = width
        imageHeight = height
        notifyDataSetChanged()
    }

    fun setCategoryList(listCate: List<Category>){
        this.listCate = listCate
        notifyDataSetChanged()
    }
    class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtCateName = itemView.findViewById<TextView>(R.id.tvCategoryName)
        val cateImg = itemView.findViewById<ImageView>(R.id.img_meal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.category_card,
                parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return listCate.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.itemView.apply {
            holder.txtCateName.text = listCate[position].strCategory
            Glide.with(holder.itemView).load(listCate[position].strCategoryThumb).into(holder.cateImg)

            val layoutParams = holder.cateImg.layoutParams
            layoutParams.width = imageWidth
            layoutParams.height = imageHeight
            holder.cateImg.layoutParams = layoutParams

            holder.itemView.setOnClickListener {
                onClickItem.onClickCate(position)
            }
        }
    }

    interface OnClickCategory{
        fun onClickCate(pos: Int)
    }
}
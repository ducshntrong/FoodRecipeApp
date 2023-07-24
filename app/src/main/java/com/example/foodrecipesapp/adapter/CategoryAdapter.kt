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

class CategoryAdapter(context: Context,private var listCate: List<Category>, val onClickItem: OnClickCategory):
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    private var imageWidth: Int = 413
    private var imageHeight: Int = 426
    fun setSize(width: Int, height: Int){
        imageWidth = width
        imageHeight = height
        notifyDataSetChanged()
    }
    init {
        // Lấy kích thước hiện tại của màn hình từ SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val isLandscape = context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val key = if (isLandscape) "image_size_landscape_cate" else "image_size_portrait_cate"
        val defaultValue = if (isLandscape) 935 else 426
        val imageSize = sharedPreferences.getInt(key, defaultValue)
        imageWidth = imageSize
        imageHeight = imageSize
    }

    fun setCategoryList(listCate: List<Category>){
        this.listCate = listCate
        notifyDataSetChanged()
    }

    class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtCateName = itemView.findViewById<TextView>(R.id.tvCategoryName)
        val CateImg = itemView.findViewById<ImageView>(R.id.img_meal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_categories,
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
            Glide.with(holder.itemView).load(listCate[position].strCategoryThumb).into(holder.CateImg)

            val layoutParams = holder.CateImg.layoutParams
            layoutParams.width = imageWidth
            layoutParams.height = imageHeight
            holder.CateImg.layoutParams = layoutParams

            holder.itemView.setOnClickListener {
                onClickItem.onClickCate(position)
            }
        }
    }

    interface OnClickCategory{
        fun onClickCate(pos: Int)
    }
}
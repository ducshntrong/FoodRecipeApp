package com.example.foodrecipesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.data.Category

class CategoryAdapter2(private var listCate: List<Category>, val onClickItem: OnClickCategory):
    RecyclerView.Adapter<CategoryAdapter2.CategoryViewHolder>() {
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
            Glide.with(holder.itemView).load(listCate[position].strCategoryThumb).into(holder.CateImg)

            holder.itemView.setOnClickListener {
                onClickItem.onClickCate(position)
            }
        }
    }

    interface OnClickCategory{
        fun onClickCate(pos: Int)
    }
}
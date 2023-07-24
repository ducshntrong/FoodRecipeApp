package com.example.foodrecipesapp.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.data.MealDetail

class MealFavAdapter: RecyclerView.Adapter<MealFavAdapter.MealFavViewHolder>() {
    private var favList:List<MealDetail> = ArrayList()
    private lateinit var onClick: onItemClick
    private var imageWidth: Int = 495
    private var imageHeight: Int = 495
    fun setSize(width: Int, height: Int){
        imageWidth = width
        imageHeight = height
        notifyDataSetChanged()
    }
    fun setOnItemClick(onClick: onItemClick){
        this.onClick = onClick
    }
    fun setFavList(meal :List<MealDetail>){
        this.favList = meal
        notifyDataSetChanged()
    }

    fun getMealByPosition(pos: Int): MealDetail {
        return favList[pos]
    }
    class MealFavViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val imgFav = itemView.findViewById<ImageView>(R.id.img_meal)
        val tv_meal_name = itemView.findViewById<TextView>(R.id.tv_meal_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealFavViewHolder {
        return MealFavViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.single_meal_card, parent, false))
    }

    override fun getItemCount(): Int {
        return favList.size
    }

    override fun onBindViewHolder(holder: MealFavViewHolder, position: Int) {
        holder.itemView.apply {
            holder.tv_meal_name.text = favList[position].strMeal
            Glide.with(holder.itemView).load(favList[position].strMealThumb).into(holder.imgFav)

            // Cập nhật kích thước cho ImageView
            val layoutParams = holder.imgFav.layoutParams
            layoutParams.width = imageWidth
            layoutParams.height = imageHeight
            holder.imgFav.layoutParams = layoutParams

            holder.itemView.setOnClickListener {
                onClick.onClick(position)
            }
        }
    }

    interface onItemClick{
        fun onClick(pos: Int)
    }
}
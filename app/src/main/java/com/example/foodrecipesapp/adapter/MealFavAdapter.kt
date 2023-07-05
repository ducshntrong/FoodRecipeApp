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
//            if (favList[position].strMealThumb.contains("themealdb.com")){
//                Glide.with(holder.itemView).load(favList[position].strMealThumb).into(holder.imgFav)
//            }else{
//                val bytes = Base64.decode(favList[position].strMealThumb, Base64.DEFAULT)
//                val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
//                holder.imgFav.setImageBitmap(bitmap)
//            }
            holder.itemView.setOnClickListener {
                onClick.onClick(position)
            }
        }
    }

    interface onItemClick{
        fun onClick(pos: Int)
    }
}
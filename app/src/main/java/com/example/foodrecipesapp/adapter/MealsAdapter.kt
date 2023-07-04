package com.example.foodrecipesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.data.Meal

class MealsAdapter(private var list: List<Meal>, val onClick: OnClickMeal)
    :RecyclerView.Adapter<MealsAdapter.MealViewHolder>(){
    private lateinit var onLongClick: OnLongClickMeal
    fun setOnLongClickMeal(onLongClick: OnLongClickMeal){
        this.onLongClick = onLongClick
    }
    class MealViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_meal_name = itemView.findViewById<TextView>(R.id.tv_meal_name)
        val img_meal = itemView.findViewById<ImageView>(R.id.img_meal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        return MealViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.meal_card, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.itemView.apply {
            holder.tv_meal_name.text = list[position].strMeal
            Glide.with(holder.itemView).load(list[position].strMealThumb).into(holder.img_meal)
            holder.itemView.setOnClickListener {
                onClick.onClick(position)
            }
            holder.itemView.setOnLongClickListener {
                onLongClick.onLongClick(list[position])
                true
            }
        }
    }
    interface OnClickMeal{
        fun onClick(position: Int)
    }
    interface OnLongClickMeal{
        fun onLongClick(meal: Meal)
    }
}
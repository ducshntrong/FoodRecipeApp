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

class PopularAdapter: RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {
    private var mealsList:List<Meal> = ArrayList()
    private lateinit var onLongPoClick: OnLongClickPopular
    private lateinit var onClick: OnClickPopular
    fun setOnLongPopuClick(onClickPopular: OnLongClickPopular){
        this.onLongPoClick = onClickPopular
    }
    fun setOnPopuClick(onClickPopular: OnClickPopular){
        this.onClick = onClickPopular
    }
    fun setMealList(mealsList: List<Meal>){
        this.mealsList = mealsList
        notifyDataSetChanged()
    }
    class PopularViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val imgPopular = itemView.findViewById<ImageView>(R.id.img_popular_meal)
        val tvDinnerRecipe = itemView.findViewById<TextView>(R.id.tvDinnerRecipe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.popular_item_layout,
        parent,false))
    }

    override fun getItemCount(): Int {
        return mealsList.size
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        holder.itemView.apply {
            Glide.with(holder.itemView).load(mealsList[position].strMealThumb).into(holder.imgPopular)
            holder.tvDinnerRecipe.text = mealsList[position].strMeal
            holder.itemView.setOnClickListener {
                onClick.onItemClick(position)
            }
            holder.itemView.setOnLongClickListener {
                onLongPoClick.onLongItemClick(mealsList[position])
                true
            }
        }
    }
    interface OnClickPopular {
        fun onItemClick(pos:Int)
    }
    interface OnLongClickPopular{
        fun onLongItemClick(meal: Meal)
    }
}
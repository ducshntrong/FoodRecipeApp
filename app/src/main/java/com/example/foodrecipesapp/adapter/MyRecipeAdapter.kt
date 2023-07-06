package com.example.foodrecipesapp.adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodrecipesapp.R
import com.example.foodrecipesapp.activity.DetailRecipeActivity
import com.example.foodrecipesapp.data.MealDB

class MyRecipeAdapter(val con: Context) : RecyclerView.Adapter<MyRecipeAdapter.MyRecipeViewHolder>(){
    private var mealsList:ArrayList<MealDB> = ArrayList()
    lateinit var onClick: onItemClick
    fun setOnItemClick(onClick: onItemClick){
        this.onClick = onClick
    }
    fun setMealList(mealsList: ArrayList<MealDB>){
        this.mealsList = mealsList
        notifyDataSetChanged()
    }
    class MyRecipeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imgMeal = itemView.findViewById<ImageView>(R.id.img_meal)
        val tvDesc = itemView.findViewById<TextView>(R.id.txtMoTa)
        val txtStrMeal = itemView.findViewById<TextView>(R.id.txtStrMeal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecipeViewHolder {
        return MyRecipeViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_meal, parent,false))
    }

    override fun getItemCount(): Int {
        return mealsList.size
    }

    fun getIdByPosition(pos: Int): String {
        return mealsList[pos].idMeal!!
    }
    fun getMealByPosition(pos: Int): MealDB {
        return mealsList[pos]
    }

    override fun onBindViewHolder(holder: MyRecipeViewHolder, position: Int) {
        holder.itemView.apply {
            val maxLength = 40
            val limitedDescription = if (mealsList[position].strInstructions!!.length > maxLength){
                mealsList[position].strInstructions!!.substring(0,maxLength)+"..."
            } else {
                mealsList[position].strInstructions
            }
            holder.tvDesc.text = limitedDescription
            holder.txtStrMeal.text = mealsList[position].strMeal
            Glide.with(holder.itemView).load(mealsList[position].strMealThumb).into(holder.imgMeal)

            holder.itemView.setOnClickListener {
                val id = mealsList[position].idMeal
                val nameMeal = mealsList[position].strMeal
                val area = mealsList[position].strArea
                val imgThumb = mealsList[position].strMealThumb
                val yt = mealsList[position].strYoutube
                val instruction = mealsList[position].strInstructions
                val ingredient = mealsList[position].strIngredients

                val i = Intent(con, DetailRecipeActivity::class.java)
                val recipe = MealDB(id,area,instruction,nameMeal,imgThumb,ingredient,yt)
                val bundle = Bundle()
                bundle.putParcelable("recipe",recipe)
                i.putExtras(bundle)
                con.startActivity(i)
            }
        }
    }

    interface onItemClick{
        fun onClick(pos: Int)
    }

}
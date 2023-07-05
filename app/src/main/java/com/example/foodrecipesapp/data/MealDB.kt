package com.example.foodrecipesapp.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
class MealDB(
    var idMeal: String?= null,
    var strArea: String?= null,
    var strInstructions: String?= null,
    var strMeal: String?= null,
    var strMealThumb: String?=null,
    var strIngredients: String?= null,
    var strYoutube: String?= null
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idMeal)
        parcel.writeString(strArea)
        parcel.writeString(strInstructions)
        parcel.writeString(strMeal)
        parcel.writeString(strMealThumb)
        parcel.writeString(strIngredients)
        parcel.writeString(strYoutube)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MealDB> {
        override fun createFromParcel(parcel: Parcel): MealDB {
            return MealDB(parcel)
        }

        override fun newArray(size: Int): Array<MealDB?> {
            return arrayOfNulls(size)
        }
    }
}
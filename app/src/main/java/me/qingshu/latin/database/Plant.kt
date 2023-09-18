package me.qingshu.latin.database

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.qingshu.latin.R
import me.qingshu.latin.data.PlantType

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    val chinese:String,
    val latin:String,
    val family:String,
    val category:String,
    val definition:String,
    val resId:String,
    val type:PlantType,
    val completed:Boolean = false,
    val lastCompetedTime:Long,
    val isFavorite:Boolean = false
){
    companion object{
        const val CREATE = "CREATE"

        val EMPTY get() = Plant(
            id = 0,
            chinese = "",
            latin = "",
            family = "",
            category = "",
            definition = "",
            resId = "",
            type = PlantType.EMPTY,
            lastCompetedTime = 0L
        )
    }

    fun label(context: Context):String{
        when(type){
            PlantType.TREE -> R.string.category_tree
            else -> R.string.category_flower
        }.also {
            return context.getString(it)
        }
    }
}
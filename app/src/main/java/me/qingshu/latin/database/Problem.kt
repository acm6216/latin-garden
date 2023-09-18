package me.qingshu.latin.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import me.qingshu.latin.data.PlantType

@Entity("problems")
data class Problem(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    val resId:String,
    val problemDetail:String,
    val a:String,
    val b:String,
    val c:String,
    val d:String,
    val answer:String,
    val type:PlantType
)

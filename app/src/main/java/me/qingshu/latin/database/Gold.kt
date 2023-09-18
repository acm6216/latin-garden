package me.qingshu.latin.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "golds")
data class Gold(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    val author:String,
    val sentence:String,
    val subtitle:String
)
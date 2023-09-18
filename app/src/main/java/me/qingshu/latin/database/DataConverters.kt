package me.qingshu.latin.database

import androidx.room.TypeConverter
import me.qingshu.latin.data.PlantType

class DataConverters {

    @TypeConverter
    fun intToPlantType(value:Int): PlantType {
        PlantType.values().forEach {
            if(it.ordinal == value) return it
        }
        return PlantType.TREE
    }

    @TypeConverter
    fun plantTypeToInt(plantType: PlantType):Int{
        return plantType.ordinal
    }
}
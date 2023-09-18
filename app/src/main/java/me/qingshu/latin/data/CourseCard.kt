package me.qingshu.latin.data

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import me.qingshu.latin.R

data class CourseCard(
    @DrawableRes val icon:Int,
    @StringRes val title:Int,
    val max:Int,
    val progress:Int,
    @StringRes val contentDescription:Int,
    val type: CourseType
){
    fun contentLabel(context: Context) = context.getString(contentDescription)

    fun label() = "$progress/$max"

    companion object{
        val EMPTY = CourseCard(0,0,0,0,0,CourseType.UN_DEFINE)
    }
}
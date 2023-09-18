package me.qingshu.latin.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.annotation.AttrRes
import androidx.annotation.IntegerRes
import androidx.annotation.InterpolatorRes
import androidx.core.content.res.use
import me.qingshu.latin.application

inline val Int.dp: Int get() = run { toFloat().dp }
inline val Float.dp: Int get() = run {
    val scale: Float = application.resources.displayMetrics.density
    (this * scale + 0.5f).toInt()
}

fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)

val Context.shortAnimTime: Int
    get() = getInteger(android.R.integer.config_shortAnimTime)

fun Context.sharedPreferences(): SharedPreferences = defaultSharedPreferences(this)

fun Context.getInterpolator(@InterpolatorRes id: Int): Interpolator =
    AnimationUtils.loadInterpolator(this,id)

@SuppressLint("Recycle")
fun Context.themeInterpolator(@AttrRes attr: Int): Interpolator {
    return AnimationUtils.loadInterpolator(
        this,
        obtainStyledAttributes(intArrayOf(attr)).use {
            it.getResourceId(0, android.R.interpolator.fast_out_slow_in)
        }
    )
}
package me.qingshu.latin.extensions

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

fun defaultSharedPreferences(context: Context): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(context)

inline fun SharedPreferences.edit(func: SharedPreferences.Editor.() -> Unit) {
    val editor: SharedPreferences.Editor = edit()
    editor.func()
    editor.apply()
}
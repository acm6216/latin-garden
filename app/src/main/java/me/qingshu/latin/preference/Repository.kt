package me.qingshu.latin.preference

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.preference.PreferenceManager
import me.qingshu.latin.R

class Repository(private val context: Context) {

    fun initializeDefaultValues(): Unit = defaultValues(context, R.xml.settings, false)

    fun registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: OnSharedPreferenceChangeListener): Unit =
            sharedPreferences().registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)

    fun stringAsInteger(key: Int, defaultValue: Int): Int =
        string(key, defaultValue.toString()).toInt()

    fun string(key: Int, defaultValue: String): String {
        val keyValue: String = context.getString(key)
        return try {
            sharedPreferences().getString(keyValue, defaultValue) ?: defaultValue
        } catch (e: Exception) {
            sharedPreferences().edit { putString(keyValue, defaultValue) }
            defaultValue
        }
    }

    fun changed(key:String,keyId:Int) = context.getString(keyId)==key

    fun string(key: Int, defRes: Int): String {
        val keyValue: String = context.getString(key)
        val defaultValue = context.getString(defRes)
        return try {
            sharedPreferences().getString(keyValue, defaultValue) ?: defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }

    fun boolean(key: Int, defaultValue: Boolean): Boolean {
        val keyValue: String = context.getString(key)
        return try {
            sharedPreferences().getBoolean(keyValue, defaultValue)
        } catch (e: Exception) {
            sharedPreferences().edit { putBoolean(keyValue, defaultValue) }
            defaultValue
        }
    }

    fun resourceInteger(key: Int): Int = context.resources.getInteger(key)

    fun integer(key: Int, defaultValue: Int): Int {
        val keyValue: String = context.getString(key)
        return try {
            sharedPreferences().getInt(keyValue, defaultValue)
        } catch (e: Exception) {
            sharedPreferences().edit { putString(keyValue, defaultValue.toString()) }
            defaultValue
        }
    }

    private fun defaultValues(context: Context, resId: Int, readAgain: Boolean): Unit =
        PreferenceManager.setDefaultValues(context, resId, readAgain)

    private fun defaultSharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    private fun sharedPreferences(): SharedPreferences = defaultSharedPreferences(context)

    private inline fun SharedPreferences.edit(func: SharedPreferences.Editor.() -> Unit) {
        val editor: SharedPreferences.Editor = edit()
        editor.func()
        editor.apply()
    }
}
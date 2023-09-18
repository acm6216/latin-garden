package me.qingshu.latin.preference

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import me.qingshu.latin.R
import me.qingshu.latin.data.SortBy
import me.qingshu.latin.extensions.findOne

class Settings(private val repository: Repository) {

    fun initializeDefaultValues(): Unit = repository.initializeDefaultValues()

    fun registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: OnSharedPreferenceChangeListener): Unit =
        repository.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)

    fun privacyPolicyStatus():Boolean = repository.boolean(R.string.privacy_policy_status_key,false)

    fun quizCount():Int = repository.integer(R.string.setting_learning_quiz_count_key,repository.resourceInteger(R.integer.setting_learning_quiz_count_default))
    fun quizCountChanged(key: String,call: (Int) -> Unit){
        if(repository.changed(key,R.string.setting_learning_quiz_count_key)){
            call.invoke(quizCount())
        }
    }

    fun repeatMode():String = repository.string(
        R.string.setting_repeat_mode_key,
        R.string.setting_repeat_mode_default
    )

    fun autoPlay():Boolean = repository.boolean(
        R.string.setting_playback_auto_key,
        false
    )

    fun repeatModeChanged(key:String,call:(String)->Unit){
        if(repository.changed(key, R.string.setting_repeat_mode_key)){
            call.invoke(repeatMode())
        }
    }

    fun autoPlayChanged(key: String,call: (Boolean) -> Unit){
        if(repository.changed(key,R.string.setting_playback_auto_key)){
            call.invoke(autoPlay())
        }
    }

    fun playbackSpeed(): String = repository.string(
        R.string.setting_playback_speed_key,
        R.string.setting_playback_speed_default
    )

    fun sortChanged(key: String,call:(SortBy)->Unit) {
        if(repository.changed(key, R.string.setting_sort_by_key)){
            call.invoke(sortBy())
        }
    }

    fun playbackSpeedChanged(key: String,call:(String)->Unit) {
        if(repository.changed(key, R.string.setting_playback_speed_key)){
            call.invoke(playbackSpeed())
        }
    }

    fun sortBy(): SortBy = find(SortBy.values(), R.string.setting_sort_by_key, SortBy.CHINESE)

    private fun <T : Enum<T>> find(values: Array<T>, key: Int, defaultValue: T): T {
        val value = repository.stringAsInteger(key, defaultValue.ordinal)
        return findOne(values, value, defaultValue)
    }

}
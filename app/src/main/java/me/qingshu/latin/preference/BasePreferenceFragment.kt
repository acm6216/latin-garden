package me.qingshu.latin.preference

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.preference.*
import androidx.recyclerview.widget.RecyclerView
import me.qingshu.latin.extensions.applySystemWindowInsetsPadding

abstract class BasePreferenceFragment : PreferenceFragmentCompat() {

    abstract fun isShowIcon():Boolean

    open fun apply(recyclerView: RecyclerView){}

    open fun windowsInset():Boolean = false

    private fun setAllPreferencesToAvoidHavingExtraSpace(preference: Preference) {
        preference.isIconSpaceReserved = isShowIcon()
        if (preference is PreferenceGroup)
            preference.forEach {
                setAllPreferencesToAvoidHavingExtraSpace(it)
            }
    }

    override fun setPreferenceScreen(preferenceScreen: PreferenceScreen) {
        setAllPreferencesToAvoidHavingExtraSpace(preferenceScreen)
        super.setPreferenceScreen(preferenceScreen)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this::class.java.superclass.superclass.getDeclaredField("mList").run {
            isAccessible = true
            (get(this@BasePreferenceFragment) as RecyclerView).apply {
                overScrollMode = View.OVER_SCROLL_NEVER
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    scrollIndicators = View.SCROLL_INDICATOR_BOTTOM or View.SCROLL_INDICATOR_TOP
                }
                apply(this)
                applySystemWindowInsetsPadding(
                    previousApplyLeft = false,
                    previousApplyTop = false,
                    previousApplyRight = false,
                    previousApplyBottom = false,
                    applyLeft = false,
                    applyTop = windowsInset(),
                    applyRight = false,
                    applyBottom = windowsInset()
                )
            }
        }
    }

    protected fun Int.click(block:(Preference)->Unit){
        get<Preference>()?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            block.invoke(it)
            true
        }
    }

    protected fun <T : Preference> Int.get(): T? = findPreference(getString(this))

}
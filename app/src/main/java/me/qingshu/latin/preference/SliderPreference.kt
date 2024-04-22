package me.qingshu.latin.preference

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.google.android.material.slider.Slider
import me.qingshu.latin.R
import me.qingshu.latin.extensions.edit

class SliderPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : Preference(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var summaryView:TextView
    private lateinit var titleView:TextView
    private lateinit var slider: Slider
    private lateinit var iconView: ImageView

    private val sliderTouchListener = object :Slider.OnSliderTouchListener{
        override fun onStartTrackingTouch(slider: Slider) {}

        override fun onStopTrackingTouch(slider: Slider) {
            summaryView.text = slider.value.let { "${it}倍" }
            sharedPreferences?.edit {
                putString(key, slider.value.toString())
            }
        }
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        titleView = holder.findViewById(R.id.title) as TextView
        summaryView = holder.findViewById(R.id.summary) as TextView
        slider = holder.findViewById(R.id.widget) as Slider
        iconView = holder.findViewById(R.id.icon) as ImageView
        titleView.text = title.toString()
        iconView.setImageDrawable(icon)
        summaryView.text = sharedPreferences?.getString(key,"1")?.also {
            slider.value = it.toFloat()
        }.let { "${it}倍" }
        slider.addOnSliderTouchListener(sliderTouchListener)
    }

    override fun onDetached() {
        super.onDetached()
        slider.removeOnSliderTouchListener(sliderTouchListener)
    }

}
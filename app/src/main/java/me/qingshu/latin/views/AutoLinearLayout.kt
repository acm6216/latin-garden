package me.qingshu.latin.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.TintTypedArray
import me.qingshu.latin.R

@SuppressLint("RestrictedApi")
class AutoLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
):LinearLayoutCompat(context, attrs, defStyleAttr) {

    private var isLand:Boolean = false

    init {
        val a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.AutoLinearLayout, defStyleAttr, 0)
        isLand = a.getBoolean(R.styleable.AutoLinearLayout_is_land,false)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if(isLand) super.onMeasure(heightMeasureSpec, heightMeasureSpec)
        else super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}
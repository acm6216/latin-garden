package me.qingshu.latin.views.rotate3d

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import me.qingshu.latin.views.rotate3d.Rotate3DAnimation.AnimationUpdateListener

class Rotate3DLayout  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
):FrameLayout(context, attrs, defStyleAttr) {

    private var mDefaultLayout: DefaultLayout? = null
    private var mReverseLayout: ReverseLayout? = null

    private val startRotate3DAnimation = Rotate3DAnimation(0f, 180f).apply {
        duration = 500
        interpolator = LinearInterpolator()
        fillAfter = true
        setUpdateListener(object : AnimationUpdateListener {
            override fun onProgressUpdate(progress: Float, value: Float) {
                if (mDefaultLayout != null && mReverseLayout != null && progress >= 0.5 && mDefaultLayout?.visibility == VISIBLE) {
                    mDefaultLayout?.visibility = GONE
                    mReverseLayout?.visibility = VISIBLE
                }
                running = progress.toDouble() != 0.0 && progress.toDouble() != 1.0
            }
        })
    }

    private val endRotate3DAnimation = Rotate3DAnimation(180f, 0f).apply {
        duration = 500
        interpolator = LinearInterpolator()
        fillAfter = true
        setUpdateListener(object : AnimationUpdateListener {
            override fun onProgressUpdate(progress: Float, value: Float) {
                if (mDefaultLayout != null && mReverseLayout != null && progress >= 0.5 && mReverseLayout?.visibility == VISIBLE) {
                    mDefaultLayout?.visibility = VISIBLE
                    mReverseLayout?.visibility = GONE
                }
                running = progress.toDouble() != 0.0 && progress.toDouble() != 1.0
            }
        })
    }

    private val resetEndRotate3DAnimation = Rotate3DAnimation(180f, 0f).apply {
        duration = 10
        interpolator = LinearInterpolator()
        fillAfter = true
        setUpdateListener(object : AnimationUpdateListener {
            override fun onProgressUpdate(progress: Float, value: Float) {
                if (mDefaultLayout != null && mReverseLayout != null && progress >= 0.5 && mReverseLayout?.visibility == VISIBLE) {
                    mDefaultLayout?.visibility = VISIBLE
                    mReverseLayout?.visibility = GONE
                }
                running = progress.toDouble() != 0.0 && progress.toDouble() != 1.0
            }
        })
    }

    private var running = false

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        for (i in 0 until childCount) {
            when(val child = getChildAt(i)){
                is DefaultLayout -> mDefaultLayout = child
                is ReverseLayout -> mReverseLayout = child
            }
            if (mDefaultLayout!=null && mReverseLayout!=null) {
                break
            }
        }
    }

    fun rotate3D() {
        if (mDefaultLayout == null || mReverseLayout == null || running) return
        startAnimation(
            if (mDefaultLayout?.visibility == VISIBLE)
                startRotate3DAnimation
            else endRotate3DAnimation
        )
    }

    fun end(){
        val isVisible = mDefaultLayout?.let { it.visibility== View.GONE }?:return
        if(isVisible) startAnimation(resetEndRotate3DAnimation)
    }
}
package me.qingshu.latin.views.rotate3d

import android.graphics.Camera
import android.view.animation.Animation
import android.view.animation.Transformation
import kotlin.math.abs

class Rotate3DAnimation(private val mFromDegrees:Float,private val mToDegrees:Float): Animation() {

    private var mCenterX = 0
    private var mCenterY:Int = 0
    private val mCamera = Camera()
    private var mUpdateListener: AnimationUpdateListener? = null
    private var mWidth = 0

    fun setUpdateListener(updateListener: AnimationUpdateListener) {
        mUpdateListener = updateListener
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        mWidth = width
        mCenterX = width / 2
        mCenterY = height / 2
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val egress = mFromDegrees + interpolatedTime * (mToDegrees - mFromDegrees)
        mUpdateListener?.onProgressUpdate(interpolatedTime, egress)
        val matrix = t.matrix
        mCamera.save()
        mCamera.translate(0f, 0f, 
            if(interpolatedTime >= 0.5) 
                abs(interpolatedTime - 1) / 0.5f * mWidth / 2
            else interpolatedTime / 0.5f * mWidth / 2
        )
        mCamera.rotateY(egress)
        mCamera.getMatrix(matrix)
        matrix.preTranslate(-mCenterX.toFloat(), -mCenterY.toFloat())
        matrix.postTranslate(mCenterX.toFloat(), mCenterY.toFloat())
        mCamera.restore()
        super.applyTransformation(interpolatedTime, t)
    }
    
    interface AnimationUpdateListener {
        fun onProgressUpdate(progress: Float, value: Float)
    }
}
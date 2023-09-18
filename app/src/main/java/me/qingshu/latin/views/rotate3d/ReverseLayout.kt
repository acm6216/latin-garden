package me.qingshu.latin.views.rotate3d

import android.content.Context
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.widget.FrameLayout

class ReverseLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
):FrameLayout(context, attrs, defStyleAttr) {

    private var mCamera: Camera = Camera()

    init {
        visibility = INVISIBLE
    }

    override fun dispatchDraw(canvas: Canvas) {
        mCamera.save()
        canvas.save()
        mCamera.rotateY(180f)
        val matrix = Matrix()
        mCamera.getMatrix(matrix)
        matrix.preTranslate(-width / 2f, -height / 2f)
        matrix.postTranslate(width / 2f, height / 2f)
        canvas.setMatrix(matrix)
        mCamera.restore()
        super.dispatchDraw(canvas)
        canvas.restore()
    }

}
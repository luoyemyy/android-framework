package com.github.luoyemyy.picker.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView

open class PreviewImageView(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : ImageView(context, attributeSet, defStyleAttr, defStyleRes) {

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : this(context, attributeSet, defStyleAttr, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0, 0)
    constructor(context: Context) : this(context, null, 0, 0)

    private var mMatrix = matrix
    private var mResetMatrix = matrix
    private var mInitMatrix = false
    private var mImageViewListener: ImageViewListener? = null
    private val mScaleGestureDetector = ScaleGestureDetector(context, ScaleGestureListener())
    private val mGestureDetector = GestureDetector(context, GestureListener())

    fun setImageViewListener(listener: ImageViewListener) {
        mImageViewListener = listener
    }

    private fun initMatrix() {
        if (!mInitMatrix) {
            mInitMatrix = true
            mMatrix.set(imageMatrix)
            mResetMatrix = Matrix(imageMatrix)
            scaleType = ScaleType.MATRIX
            imageMatrix = mMatrix
        }
    }

    private fun getMatrixValues(matrix: Matrix): FloatArray {
        val array = FloatArray(9)
        matrix.getValues(array)
        return array
    }

    private fun reset() {

        val start = getMatrixValues(mMatrix)
        val end = getMatrixValues(mResetMatrix)

        val animator = ValueAnimator()
        animator.setObjectValues(start, end)
        animator.duration = 300
        animator.setEvaluator { fraction, _, _ ->
            val v = FloatArray(9)
            for (i in (0..8)) {
                v[i] = start[i] + (end[i] - start[i]) * fraction
            }
            Matrix().apply { setValues(v) }
        }
        animator.addUpdateListener {
            mMatrix.set(it.animatedValue as Matrix)
            imageMatrix = mMatrix
        }
        animator.start()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        mInitMatrix = false
        scaleType = ScaleType.FIT_CENTER
        super.setImageDrawable(drawable)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mGestureDetector.onTouchEvent(event)
        mScaleGestureDetector.onTouchEvent(event)
        return true
    }

    inner class ScaleGestureListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            mImageViewListener?.onChange()
            initMatrix()
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mMatrix.postScale(detector.scaleFactor, detector.scaleFactor, detector.focusX, detector.focusY)
            imageMatrix = mMatrix
            return true
        }
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            initMatrix()
            return false
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            mImageViewListener?.onChange()
            mMatrix.postTranslate(-distanceX, -distanceY)
            imageMatrix = mMatrix
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            mImageViewListener?.onChange()
            reset()
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            mImageViewListener?.onSingleTap()
            return super.onSingleTapConfirmed(e)
        }
    }

}
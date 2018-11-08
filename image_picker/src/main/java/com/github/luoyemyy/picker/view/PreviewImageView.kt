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

    protected var mMatrix = matrix
    protected var mResetMatrix = matrix
    private var mInitMatrix = false
    private var mImageViewListeners = mutableListOf<ImageViewListener>()
    private val mScaleGestureDetector = ScaleGestureDetector(context, ScaleGestureListener())
    private val mGestureDetector = GestureDetector(context, GestureListener())
    private var mChange = false

    fun addImageViewListener(listener: ImageViewListener) {
        mImageViewListeners.add(listener)
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

    protected fun getMatrixValues(matrix: Matrix): FloatArray {
        val array = FloatArray(9)
        matrix.getValues(array)
        return array
    }

    private fun reset() {
        animator(mMatrix, mResetMatrix)
    }

    protected fun animator(startMatrix: Matrix, endMatrix: Matrix) {
        val start = getMatrixValues(startMatrix)
        val end = getMatrixValues(endMatrix)

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
        if (event == null) return false
        mGestureDetector.onTouchEvent(event)
        mScaleGestureDetector.onTouchEvent(event)
        if (mChange && event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
            changeEnd()
        }
        return true
    }

    open fun changeEnd() {

    }

    fun scale(scale: Float, x: Float, y: Float) {
        mMatrix.postScale(scale, scale, x, y)
        imageMatrix = mMatrix
    }

    fun translate(x: Float, y: Float) {
        mMatrix.postTranslate(x, y)
        imageMatrix = mMatrix
    }

    inner class ScaleGestureListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            mImageViewListeners.forEach { it.onChange() }
            initMatrix()
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scale(detector.scaleFactor, detector.focusX, detector.focusY)
            mChange = true
            return true
        }
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            initMatrix()
            mChange = false
            return false
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            mImageViewListeners.forEach { it.onChange() }
            translate(-distanceX, -distanceY)
            mChange = true
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            mImageViewListeners.forEach { it.onChange() }
            reset()
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            mImageViewListeners.forEach { it.onSingleTap() }
            return super.onSingleTapConfirmed(e)
        }
    }

}
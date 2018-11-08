package com.github.luoyemyy.picker.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import kotlin.math.min

class CropImageView(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : PreviewImageView(context, attributeSet, defStyleAttr, defStyleRes) {

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : this(context, attributeSet, defStyleAttr, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0, 0)
    constructor(context: Context) : this(context, null, 0, 0)

    private var mMaskColor: Int = 0x88000000.toInt()
    private val mPaint = Paint().apply {
        color = mMaskColor
    }
    private val mStrokePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawMask(canvas)
    }

    override fun changeEnd() {
        checkBound()
    }

    private fun checkBound() {
        val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: return
        val matrixBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, imageMatrix, false)
        val w = matrixBitmap.width.toFloat()
        val h = matrixBitmap.height.toFloat()
        matrixBitmap.recycle()
        val rect = RectF(0f, 0f, w, h)
        matrixBitmap.recycle()
        imageMatrix.mapRect(rect)
        val cropRect = calculateCropSpace()
        val cropW = cropRect.right - cropRect.left
        val cropH = cropRect.bottom - cropRect.top

        val startValues = getMatrixValues(mMatrix)

        val dw = w - cropW
        val dh = h - cropH
        if (dw < 0 || dh < 0) {
            if (dw < dh) {
                val scale = cropW / w
                mMatrix.postScale(scale, scale)
            } else {
                val scale = cropH / h
                mMatrix.postScale(scale, scale)
            }
        }
        val dst = RectF()
        mMatrix.mapRect(dst,rect)
        Log.e("CropImageView", "checkBound:  $rect")
        Log.e("CropImageView", "checkBound:  $cropRect")
        if (rect.left > cropRect.left) {
            mMatrix.postTranslate(cropRect.left - rect.left, 0f)
        } else if (rect.right < cropRect.right) {
            mMatrix.postTranslate(cropRect.right - rect.right, 0f)
        }
        if (rect.top > cropRect.top) {
            mMatrix.postTranslate(0f, cropRect.top - rect.top)
        } else if (rect.bottom < cropRect.bottom) {
            mMatrix.postTranslate(0f, cropRect.bottom - rect.bottom)
        }

        animator(Matrix().apply { setValues(startValues) }, mMatrix)
    }

    private fun drawMask(canvas: Canvas?) {
        val cropRect = calculateCropSpace()
        val leftRect = RectF(0f, cropRect.top, cropRect.left, cropRect.bottom)
        val topRect = RectF(0f, 0f, width.toFloat(), cropRect.top)
        val rightRect = RectF(cropRect.right, cropRect.top, width.toFloat(), cropRect.bottom)
        val bottomRect = RectF(0f, cropRect.bottom, width.toFloat(), height.toFloat())

        canvas?.drawRect(leftRect, mPaint)
        canvas?.drawRect(topRect, mPaint)
        canvas?.drawRect(rightRect, mPaint)
        canvas?.drawRect(bottomRect, mPaint)
        canvas?.drawRect(cropRect, mStrokePaint)
    }

    private fun calculateCropSpace(): RectF {
        val space = min(height, width) / 8
        return RectF((width / 2 - 3 * space).toFloat(), (height / 2 - 3 * space).toFloat(), (width / 2 + 3 * space).toFloat(), (height / 2 + 3 * space).toFloat())
    }

    fun crop() {
        val cropRect = calculateCropSpace()
        val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: return
        Log.e("CropImageView", "crop:  ${bitmap.width},${bitmap.height}")

        val rect1 = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        imageMatrix.mapRect(rect1)
        Log.e("CropImageView", "crop1:  ${rect1}")

        val rect2 = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        mMatrix.mapRect(rect2)
        Log.e("CropImageView", "crop2:  ${rect2}")

        val rect3 = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        mResetMatrix.mapRect(rect3)
        Log.e("CropImageView", "crop3:  ${rect3}")

//        Log.e("CropImageView", "crop:1  ${bitmap.byteCount}")
        val matrixBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, imageMatrix, false)
        Log.e("CropImageView", "crop:  ${matrixBitmap.width},${matrixBitmap.height}")
//        Log.e("CropImageView", "crop:2  ${matrixBitmap.byteCount} ")
//        val cropBitmap = Bitmap.createBitmap(matrixBitmap, cropRect.left.toInt(), cropRect.top.toInt(), (cropRect.right - cropRect.left).toInt(), (cropRect.bottom - cropRect.top).toInt())
//        Log.e("CropImageView", "crop:3  ${cropBitmap.byteCount}")
//        setImageBitmap(cropBitmap)
    }
}
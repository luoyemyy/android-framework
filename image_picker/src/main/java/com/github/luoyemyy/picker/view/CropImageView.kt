package com.github.luoyemyy.picker.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import com.github.luoyemyy.picker.ImagePicker
import kotlin.math.min

class CropImageView(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : PreviewImageView(context, attributeSet, defStyleAttr, defStyleRes) {

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : this(context, attributeSet, defStyleAttr, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0, 0)
    constructor(context: Context) : this(context, null, 0, 0)

    private var mCropType = ImagePicker.option.cropType
    private var mCropSize = ImagePicker.option.cropSize
    private var mCropPercent = ImagePicker.option.cropPercent
    private var mCropRatio = ImagePicker.option.cropRatio
    private var mMaskColor: Int = 0x80000000.toInt()
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

    /**
     * 画裁剪区域
     */
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

    private fun cropSize(): Pair<Int, Int> {
        when (mCropType) {
            1 -> return if (width < height) {
                val w = min(mCropSize, width)
                val h = min((w * mCropRatio).toInt(), height)
                Pair(w, h)
            } else {
                val h = min(mCropSize, height)
                val w = min((h * mCropRatio).toInt(), width)
                Pair(w, h)
            }
            2 -> return if (width < height) {
                val w = (width * mCropPercent).toInt()
                val h = min((w * mCropRatio).toInt(), height)
                Pair(w, h)
            } else {
                val h = (height * mCropPercent).toInt()
                val w = min((h * mCropRatio).toInt(), width)
                Pair(w, h)
            }
            else -> return Pair(0, 0)
        }

    }

    /**
     * 计算裁剪区域
     */
    private fun calculateCropSpace(): RectF {
        val (x, y) = cropSize()
        return RectF((width / 2 - x / 2).toFloat(), (height / 2 - y / 2).toFloat(), (width / 2 + x / 2).toFloat(), (height / 2 + y / 2).toFloat())
    }

    /**
     * 变化结束，保证图片完全覆盖裁剪区域
     */
    override fun changeEnd() {
        matchBounds()
    }

    /**
     * 保证图片完全覆盖裁剪区域
     */
    private fun matchBounds() {
        initMatrixType()
        val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: return
        val src = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        var scale = RectF(src)
        val matrix = Matrix(imageMatrix)
        matrix.mapRect(src)
        val w = src.right - src.left
        val h = src.bottom - src.top
        val crop = calculateCropSpace()
        val cw = crop.right - crop.left
        val ch = crop.bottom - crop.top

        val startValues = getMatrixValues(matrix)

        //缩放保持高度和宽度不小于裁剪区域
        val dw = w - cw
        val dh = h - ch
        if (dw < 0 || dh < 0) {
            val s = if (dw < dh) {
                Math.ceil(cw.toDouble()) / w
            } else {
                Math.ceil(ch.toDouble()) / h
            }.toFloat()
            matrix.postScale(s, s)
            matrix.mapRect(scale)
        } else {
            scale = src
        }

        //平移保证裁剪区域在图片内
        if (scale.left > crop.left) {
            matrix.postTranslate(crop.left - scale.left, 0f)
        } else if (scale.right < crop.right) {
            matrix.postTranslate(crop.right - scale.right, 0f)
        }
        if (scale.top > crop.top) {
            matrix.postTranslate(0f, crop.top - scale.top)
        } else if (scale.bottom < crop.bottom) {
            matrix.postTranslate(0f, crop.bottom - scale.bottom)
        }

        val endValues = getMatrixValues(matrix)
        if (startValues.toString() != endValues.toString()) {
            animator(Matrix().apply { setValues(startValues) }, matrix)
        }
    }

    fun crop(success: (Bitmap) -> Unit, failure: (Throwable) -> Unit) {
        AsyncTask.execute {
            var cropBitmap: Bitmap? = null
            val handler = Handler(Looper.getMainLooper())
            try {
                //实际的裁剪区域坐标
                val cropRect = calculateCropSpace()
                val bitmap = (drawable as BitmapDrawable).bitmap
                val src = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
                imageMatrix.mapRect(src)//计算图片经过变换后的区域坐标

                //裁剪区域相对于图片的坐标
                val cropWidth = cropRect.right - cropRect.left
                val cropHeight = cropRect.bottom - cropRect.top
                val cropX = cropRect.left - src.left
                val cropY = cropRect.top - src.top

                //计算图片的缩放比例
                val resetValues = getResetValues()
                val currentValues = getCurrentValues()
                val scale = currentValues[Matrix.MSCALE_X] / resetValues[Matrix.MSCALE_X]

                //计算裁剪区域还原缩放比例的宽度和高度
                val cw = cropWidth / scale
                val ch = cropHeight / scale

                //计算裁剪区域还原缩放比例后的左上角坐标
                val w = cw.toInt()
                val h = ch.toInt()
                val x = (cropX - (cropWidth - cw) / 2).toInt()
                val y = (cropY - (cropHeight - ch) / 2).toInt()

                cropBitmap = Bitmap.createBitmap(bitmap, x, y, w, h, imageMatrix, false)
            } catch (e: Throwable) {
                handler.post {
                    failure(e)
                }
            } finally {
                handler.post {
                    cropBitmap?.apply {
                        success(this)
                        setImageBitmap(this)
                    }
                }
            }
        }
    }
}
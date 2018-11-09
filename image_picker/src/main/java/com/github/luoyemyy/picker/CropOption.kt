package com.github.luoyemyy.picker

class CropOption private constructor() {

    var cropType: Int = 0           // 0 不裁剪 1 按尺寸 2 按比例
    var size: Int = 0               //px 如果大于imageView的最小边，则取最小边
    var percent: Float = 0f         //相对于imageView的最小边的比例
    var ratio: Float = 0f           //x:y 比例

    companion object {

        fun default(): CropOption {
            return CropOption().apply {
                cropType = 2
                percent = 0.75f
                ratio = 1f
            }
        }

        fun create(): CropOption {
            return CropOption()
        }
    }

    fun bySize(size: Int, ratio: Float): CropOption {
        if (size <= 0) throw IllegalArgumentException("size: size > 0")
        if (ratio <= 0) throw IllegalArgumentException("ratio: ratio > 0")
        this.size = size
        this.ratio = ratio
        this.cropType = 1
        return this
    }

    fun byPercent(percent: Float, ratio: Float): CropOption {
        if (percent <= 0 || percent >= 1) throw IllegalArgumentException("percent: 0 < percent < 1")
        if (ratio <= 0) throw IllegalArgumentException("ratio: ratio > 0")
        this.percent = percent
        this.ratio = ratio
        this.cropType = 2
        return this
    }
}
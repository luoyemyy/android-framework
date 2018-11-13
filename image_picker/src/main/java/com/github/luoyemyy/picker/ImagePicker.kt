package com.github.luoyemyy.picker

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.github.luoyemyy.bus.BusManager
import com.github.luoyemyy.bus.BusMsg
import com.github.luoyemyy.bus.BusResult
import com.github.luoyemyy.ext.toList
import com.github.luoyemyy.picker.picker.PickerActivity

class ImagePicker private constructor() {

    companion object {

        internal lateinit var option: PickerOption
        internal const val PICKER_RESULT = "com.github.luoyemyy.picker.ImagePicker.PICKER_RESULT"
        internal const val ALBUM_RESULT = "com.github.luoyemyy.picker.ImagePicker.ALBUM_RESULT"
        internal const val CROP_RESULT = "com.github.luoyemyy.picker.ImagePicker.CROP_RESULT"

        /**
         * @param fileProvider external-path#Pictures
         */
        fun create(fileProvider: String): Builder {
            return Builder(fileProvider)
        }
    }

    class Builder internal constructor(fileProvider: String) {

        private val mOption = PickerOption(fileProvider)

        /**
         * @param  0 < minSelect <= maxSelect （默认1）
         */
        fun minSelect(minSelect: Int): Builder {
            mOption.minSelect = minSelect
            return this
        }

        /**
         * @param maxSelect minSelect <= maxSelect  （默认1）
         */
        fun maxSelect(maxSelect: Int): Builder {
            mOption.maxSelect = maxSelect
            return this
        }

        /**
         * @param portrait true 竖屏（默认) ；false 横屏
         */
        fun portrait(portrait: Boolean): Builder {
            mOption.portrait = portrait
            return this
        }

        /**
         * 按照固定尺寸计算裁剪区域，不超过imageView的大小
         * @param size cropSize > 0
         * @param ratio  cropRatio > 0
         */
        fun cropBySize(size: Int, ratio: Float = 1f, require: Boolean = true): Builder {
            if (size <= 0) throw IllegalArgumentException("cropSize: cropSize > 0")
            if (ratio <= 0) throw IllegalArgumentException("cropRatio: cropRatio > 0")
            mOption.cropSize = size
            mOption.cropRatio = ratio
            mOption.cropRequire = require
            mOption.cropType = 1
            return this
        }

        /**
         * 按照imageView的最小边的百分比计算裁剪区域
         * @param percent 0 < cropPercent <= 1
         * @param ratio  cropRatio > 0
         */
        fun cropByPercent(percent: Float = 0.6f, ratio: Float = 1f, require: Boolean = true): Builder {
            if (percent <= 0 || percent > 1) throw IllegalArgumentException("cropPercent: 0 < cropPercent <= 1")
            if (ratio <= 0) throw IllegalArgumentException("cropRatio: cropRatio > 0")
            mOption.cropPercent = percent
            mOption.cropRatio = ratio
            mOption.cropRequire = require
            mOption.cropType = 2
            return this
        }

        fun build(): ImagePicker {
            if (mOption.maxSelect <= 0) {
                mOption.maxSelect = 1
            }
            if (mOption.minSelect <= 0) {
                mOption.minSelect = 1
            } else if (mOption.minSelect > mOption.maxSelect) {
                mOption.minSelect = mOption.maxSelect
            }
            ImagePicker.option = mOption
            return ImagePicker()
        }
    }

    fun picker(activity: FragmentActivity, callback: (List<String>?) -> Unit) {
        BusManager.replaceCallback(activity.lifecycle, PickerInternal(callback), PICKER_RESULT)
        activity.startActivity(Intent(activity, PickerActivity::class.java))
    }

    fun picker(fragment: Fragment, callback: (List<String>?) -> Unit) {
        BusManager.replaceCallback(fragment.lifecycle, PickerInternal(callback), PICKER_RESULT)
        fragment.startActivity(Intent(fragment.requireContext(), PickerActivity::class.java))
    }

    inner class PickerInternal(private var mCallback: (List<String>?) -> Unit) : BusResult {
        override fun busResult(event: String, msg: BusMsg) {
            mCallback.invoke(msg.stringValue?.toList())
        }
    }
}
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
        internal const val CAMERA_RESULT = "com.github.luoyemyy.picker.ImagePicker.CAMERA_RESULT"
        internal const val ALBUM_RESULT = "com.github.luoyemyy.picker.ImagePicker.ALBUM_RESULT"
        internal const val CROP_RESULT = "com.github.luoyemyy.picker.ImagePicker.CROP_RESULT"
        internal const val PICKER_RESULT = "com.github.luoyemyy.picker.ImagePicker.PICKER_RESULT"

        fun create(fileProvider: String): Builder {
            return Builder().apply {
                fileProvider(fileProvider)
            }
        }
    }

    class Builder {

        private val mOption: PickerOption = PickerOption()


        internal fun fileProvider(fileProvider: String): Builder {
            mOption.fileProvider = fileProvider
            return this
        }

        fun maxSelect(maxSelect: Int): Builder {
            mOption.maxSelect = maxSelect
            return this
        }

        fun cropBySize(size: Int, ratio: Float): Builder {
            if (size <= 0) throw IllegalArgumentException("cropSize: cropSize > 0")
            if (ratio <= 0) throw IllegalArgumentException("cropRatio: cropRatio > 0")
            mOption.cropSize = size
            mOption.cropRatio = ratio
            mOption.cropType = 1
            return this
        }

        fun cropByPercent(percent: Float, ratio: Float): Builder {
            if (percent <= 0 || percent >= 1) throw IllegalArgumentException("cropPercent: 0 < cropPercent < 1")
            if (ratio <= 0) throw IllegalArgumentException("cropRatio: cropRatio > 0")
            mOption.cropPercent = percent
            mOption.cropRatio = ratio
            mOption.cropType = 2
            return this
        }


        fun build(): ImagePicker {
            ImagePicker.option = mOption
            return ImagePicker()
        }
    }

    fun picker(activity: FragmentActivity, callback: (List<String>?) -> Unit) {
        BusManager.setCallback(activity.lifecycle, PickerInternal(callback), PICKER_RESULT, CROP_RESULT)
        activity.startActivity(Intent(activity, PickerActivity::class.java))
    }

    fun picker(fragment: Fragment, callback: (List<String>?) -> Unit) {
        BusManager.setCallback(fragment.lifecycle, PickerInternal(callback), PICKER_RESULT, CROP_RESULT)
        fragment.startActivity(Intent(fragment.requireContext(), PickerActivity::class.java))
    }


    inner class PickerInternal(private val mCallback: (List<String>?) -> Unit) : BusResult {
        override fun busResult(event: String, msg: BusMsg) {
            if (PICKER_RESULT == event) {
                if (option.cropType == 0) {
                    mCallback(msg.stringValue?.toList())
                } else {

                }
            } else {
                mCallback(msg.stringValue?.toList())
            }
        }
    }
}
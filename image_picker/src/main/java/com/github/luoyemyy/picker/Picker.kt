package com.github.luoyemyy.picker

import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.github.luoyemyy.mvp.getPresenter

class Picker private constructor() {

    companion object {

        internal lateinit var bundle: PickerBundle

        fun build(builder: Builder): Picker {
            return Builder(builder).build()
        }

        fun create(): Builder {
            return Builder()
        }
    }

    class Builder(builder: Builder? = null) {

        private val mBundle: PickerBundle = builder?.mBundle ?: PickerBundle()

        fun create(): Builder {
            return Builder()
        }

        fun useCameraAndAlbum(fileProvider: String): Builder {
            mBundle.fileProvider = fileProvider
            mBundle.pickerType = 1
            return this
        }

        fun useCamera(fileProvider: String): Builder {
            mBundle.fileProvider = fileProvider
            mBundle.pickerType = 2
            return this
        }

        fun useAlbum(): Builder {
            mBundle.pickerType = 2
            return this
        }

        fun fileProvider(fileProvider: String): Builder {
            mBundle.fileProvider = fileProvider
            return this
        }

        fun maxSelect(maxSelect: Int): Builder {
            mBundle.maxSelect = maxSelect
            return this
        }

        fun crop(cropOption: CropOption = CropOption.default()): Builder {
            mBundle.cropOption = cropOption
            return this
        }

        fun build(): Picker {
            Picker.bundle = mBundle
            return Picker()
        }
    }

    fun picker(activity: FragmentActivity, callback: (List<String>?) -> Unit) {
        activity.getPresenter<PickerPresenter>().addObserver(activity, Observer {
            callback(it)
        })

        PickerFragment.start(activity)
    }

    fun picker(fragment: Fragment, callback: (List<String>?) -> Unit) {
        fragment.getPresenter<PickerPresenter>().addObserver(fragment, Observer {
            callback(it)
        })

        PickerFragment.start(fragment)
    }
}
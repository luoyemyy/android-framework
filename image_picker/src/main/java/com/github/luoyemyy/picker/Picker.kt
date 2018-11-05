package com.github.luoyemyy.picker

import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.github.luoyemyy.mvp.getPresenter

class Picker private constructor() {

    private lateinit var bundle: PickerBundle

    companion object {

        fun build(builder: Builder): Picker {
            return Builder(builder).build()
        }

        fun create(): Builder {
            return Builder()
        }
    }

    class Builder(builder: Builder? = null) {

        private val bundle: PickerBundle = builder?.bundle ?: PickerBundle()

        fun create(): Builder {
            return Builder()
        }

        fun useCameraAndAlbum(fileProvider: String): Builder {
            bundle.fileProvider = fileProvider
            bundle.pickerType = 1
            return this
        }

        fun useCamera(fileProvider: String): Builder {
            bundle.fileProvider = fileProvider
            bundle.pickerType = 2
            return this
        }

        fun useAlbum(): Builder {
            bundle.pickerType = 2
            return this
        }

        fun fileProvider(fileProvider: String): Builder {
            bundle.fileProvider = fileProvider
            return this
        }

        fun maxSelect(maxSelect: Int): Builder {
            bundle.maxSelect = maxSelect
            return this
        }

        fun crop(aspectX: Int, aspectY: Int): Builder {
            bundle.crop = true
            return this
        }

        fun build(): Picker {
            return Picker().apply {
                bundle = this@Builder.bundle
            }
        }
    }


    fun picker(activity: FragmentActivity, callback: (List<String>?) -> Unit) {
        activity.getPresenter<PickerPresenter>().addObserver(activity, Observer {
            callback(it)
        })

        PickerFragment.start(activity, bundle)
    }

    fun picker(fragment: Fragment, callback: (List<String>?) -> Unit) {
        fragment.getPresenter<PickerPresenter>().addObserver(fragment, Observer {
            callback(it)
        })

        PickerFragment.start(fragment, bundle)
    }
}
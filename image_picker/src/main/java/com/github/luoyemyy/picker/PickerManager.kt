package com.github.luoyemyy.picker

import android.arch.lifecycle.Observer
import android.support.v4.app.FragmentActivity
import com.github.luoyemyy.mvp.getPresenter

object PickerManager {

    class PickerBuilder {

        private val bundle: PickerBundle = PickerBundle()

        fun create(): PickerBuilder {
            return PickerBuilder()
        }

        fun maxSelect(maxSelect: Int): PickerBuilder {
            bundle.maxSelect = maxSelect
            return this
        }

        fun crop(crop: Boolean, aspectX: Int, aspectY: Int): PickerBuilder {
            bundle.crop = crop
            return this
        }
    }

    fun picker(activity: FragmentActivity, callback: (List<String>?) -> Unit) {
        activity.getPresenter<PickerPresenter>().addObserver(activity, Observer {
            callback(it)
        })

        PickerFragment.start(activity, PickerBundle())
    }
}
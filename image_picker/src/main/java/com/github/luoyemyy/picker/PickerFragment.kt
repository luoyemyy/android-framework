package com.github.luoyemyy.picker

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import com.github.luoyemyy.bus.BusManager
import com.github.luoyemyy.bus.BusMsg
import com.github.luoyemyy.bus.BusResult
import com.github.luoyemyy.ext.toList
import com.github.luoyemyy.mvp.getPresenter
import com.github.luoyemyy.picker.picker.PickerActivity

class PickerFragment : DialogFragment(), BusResult {

    companion object {

        internal const val PICKER_RESULT = "picker_result"

        fun start(activity: FragmentActivity) {
            start(activity.supportFragmentManager)
        }

        fun start(fragment: Fragment) {
            start(fragment.childFragmentManager)
        }

        private fun start(fragmentManager: FragmentManager) {
            fragmentManager.beginTransaction().add(PickerFragment(), null).commit()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        BusManager.setCallback(lifecycle, this, PICKER_RESULT)
        startActivity(Intent(activity, PickerActivity::class.java))
    }

    override fun busResult(event: String, msg: BusMsg) {
        getPresenter<PickerPresenter>().postValue(msg.stringValue?.toList())
    }

    private fun close() {
        (parentFragment?.childFragmentManager
                ?: requireActivity().supportFragmentManager).beginTransaction().remove(this).commit()
    }
}
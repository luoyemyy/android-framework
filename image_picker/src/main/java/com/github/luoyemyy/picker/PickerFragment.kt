package com.github.luoyemyy.picker

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.github.luoyemyy.bus.BusManager
import com.github.luoyemyy.bus.BusMsg
import com.github.luoyemyy.bus.BusResult
import com.github.luoyemyy.ext.toList
import com.github.luoyemyy.mvp.getPresenter
import com.github.luoyemyy.picker.picker.PickerActivity

class PickerFragment : DialogFragment(), BusResult {

    companion object {

        internal const val PICKER_RESULT = "picker_result"

        fun start(activity: FragmentActivity, pickerBundle: PickerBundle) {
            activity.supportFragmentManager.beginTransaction().add(PickerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("pickerBundle", pickerBundle)
                }
            }, null).commit()
        }

        fun start(fragment: Fragment, pickerBundle: PickerBundle) {
            fragment.childFragmentManager.beginTransaction().add(PickerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("pickerBundle", pickerBundle)
                }
            }, null).commit()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        BusManager.setCallback(lifecycle, this, PICKER_RESULT)
        val bundle = arguments?.getParcelable<PickerBundle>("pickerBundle")
        requireActivity().startActivity(Intent(activity, PickerActivity::class.java).putExtra("pickerBundle", bundle))
    }

    override fun busResult(event: String, msg: BusMsg) {
        getPresenter<PickerPresenter>().postValue(msg.stringValue?.toList())
    }

    private fun close() {
        (parentFragment?.childFragmentManager
                ?: requireActivity().supportFragmentManager).beginTransaction().remove(this).commit()
    }
}
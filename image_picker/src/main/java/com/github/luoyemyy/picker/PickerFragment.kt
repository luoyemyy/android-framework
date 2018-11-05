package com.github.luoyemyy.picker

import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.github.luoyemyy.mvp.getPresenter
import com.github.luoyemyy.picker.album.AlbumActivity
import com.github.luoyemyy.picker.capture.CapturePresenter

class PickerFragment : Fragment() {

    private var mCancelDismiss = true
    private lateinit var mCapturePresenter: CapturePresenter
    private lateinit var mPickerBundle: PickerBundle

    companion object {

        internal const val ALBUM_REQUEST_CODE = 101
        internal const val CAPTURE_REQUEST_CODE = 102


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

        mPickerBundle = arguments?.getParcelable("pickerBundle") ?: let {
            throw NullPointerException("pickerBundle is null")
        }

        mCapturePresenter = getPresenter()
        mCapturePresenter.captureErrorFlag.observe(this, Observer {
            close()
        })

        showPickerType()
    }

    private fun showPickerType() {
        when (mPickerBundle.pickerType) {
            1 -> {
                AlertDialog.Builder(context).setItems(R.array.image_picker_type) { dialog, which ->
                    mCancelDismiss = false
                    dialog.dismiss()
                    when (which) {
                        0 -> toAlbum()
                        1 -> toCapture()
                    }
                }.setOnDismissListener {
                    if (mCancelDismiss) close()
                }.show()
            }
            2 -> toCapture()
            3 -> toAlbum()
        }
    }

    private fun toAlbum() {
        startActivityForResult(Intent(context, AlbumActivity::class.java).putExtra("pickerBundle", mPickerBundle), ALBUM_REQUEST_CODE)
    }

    private fun toCapture() {
        mCapturePresenter.capture(requireActivity(), mPickerBundle)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val presenter = parentFragment?.getPresenter()
                ?: requireActivity().getPresenter<PickerPresenter>()
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ALBUM_REQUEST_CODE) {
                presenter.postValue(data?.getStringArrayExtra("data")?.toList())
            } else if (requestCode == CAPTURE_REQUEST_CODE) {
                presenter.postValue(mCapturePresenter.captureResult(requireContext()))
            }
        }
        close()
    }


    private fun close() {
        (parentFragment?.childFragmentManager
                ?: requireActivity().supportFragmentManager).beginTransaction().remove(this).commit()
    }
}
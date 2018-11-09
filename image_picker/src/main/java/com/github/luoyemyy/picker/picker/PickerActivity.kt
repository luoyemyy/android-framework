package com.github.luoyemyy.picker.picker

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import com.github.luoyemyy.bus.BusManager
import com.github.luoyemyy.ext.toJsonString
import com.github.luoyemyy.mvp.getPresenter
import com.github.luoyemyy.picker.PickerBundle
import com.github.luoyemyy.picker.R
import com.github.luoyemyy.picker.album.AlbumActivity
import com.github.luoyemyy.picker.capture.CapturePresenter
import com.github.luoyemyy.picker.databinding.ImagePickerPickerBinding

class PickerActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ImagePickerPickerBinding
    private lateinit var mBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var mCapturePresenter: CapturePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.image_picker_picker)

        mCapturePresenter = getPresenter()
        mCapturePresenter.captureErrorFlag.observe(this, Observer {
            hideDialog()
        })

        mBinding.txtCancel.setOnClickListener(this)
        mBinding.txtAlbum.setOnClickListener(this)
        mBinding.txtCamera.setOnClickListener(this)

        initDialog()
    }

    override fun onClick(v: View?) {
        when (v) {
            mBinding.txtAlbum -> {
                toAlbum()
            }
            mBinding.txtCamera -> {
                toCapture()
            }
            mBinding.txtCancel -> {
                hideDialog()
            }
        }
    }

    private fun toCapture() {
        mCapturePresenter.capture(this)
    }

    private fun toAlbum() {
        startActivity(Intent(this, AlbumActivity::class.java))
    }

    private fun delay(millis: Long, runnable: () -> Unit) {
        Handler().postDelayed(runnable, millis)
    }

    private fun initDialog() {
        mBehavior = BottomSheetBehavior.from(mBinding.layoutSelect)
        mBehavior.isHideable = true
        mBehavior.peekHeight = 0
        mBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(p0: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_HIDDEN || state == BottomSheetBehavior.STATE_COLLAPSED) {
                    finish()
                }
            }
        })
        delay(200) { showDialog() }
    }

    private fun showDialog() {
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideDialog() {
        mBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.image_picker_dialog_in, R.anim.image_picker_dialog_out)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CapturePresenter.CAPTURE_REQUEST_CODE) {
            BusManager.post("", stringValue = mCapturePresenter.captureResult(this).toJsonString())
        }
    }
}
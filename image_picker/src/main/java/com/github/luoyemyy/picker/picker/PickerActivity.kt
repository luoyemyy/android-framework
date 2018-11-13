package com.github.luoyemyy.picker.picker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.github.luoyemyy.bus.BusManager
import com.github.luoyemyy.bus.BusMsg
import com.github.luoyemyy.bus.BusResult
import com.github.luoyemyy.ext.toJsonString
import com.github.luoyemyy.file.FileManager
import com.github.luoyemyy.mvp.getPresenter
import com.github.luoyemyy.permission.PermissionManager
import com.github.luoyemyy.picker.ImagePicker
import com.github.luoyemyy.picker.R
import com.github.luoyemyy.picker.album.AlbumActivity
import com.github.luoyemyy.picker.capture.CapturePresenter
import com.github.luoyemyy.picker.crop.CropActivity
import com.github.luoyemyy.picker.databinding.ImagePickerPickerBinding

class PickerActivity : AppCompatActivity(), View.OnClickListener, BusResult {

    private lateinit var mBinding: ImagePickerPickerBinding
    private lateinit var mBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var mCapturePresenter: CapturePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.image_picker_picker)

        mCapturePresenter = getPresenter()

        mBinding.txtCancel.setOnClickListener(this)
        mBinding.txtAlbum.setOnClickListener(this)
        mBinding.txtCamera.setOnClickListener(this)

        initDialog()

        FileManager.initManager(application)
        BusManager.setCallback(lifecycle, this, ImagePicker.ALBUM_RESULT, ImagePicker.CROP_RESULT)
    }

    override fun onClick(v: View?) {
        when (v) {
            mBinding.txtAlbum -> {
                PermissionManager.withPass {
                    toAlbum()
                }.withDenied { future, _ ->
                    future.toSettings(this, getString(R.string.image_picker_need_storage))
                    Log.e("CapturePresenter", "权限不足，需要拥有文件读写的权限")
                }.request(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            }
            mBinding.txtCamera -> {
                PermissionManager.withPass {
                    toCapture()
                }.withDenied { future, _ ->
                    future.toSettings(this, getString(R.string.image_picker_need_camera_storage))
                    Log.e("CapturePresenter", "权限不足，需要同时拥有相机和文件读写的权限")
                }.request(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
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
            imageResult(mCapturePresenter.captureResult(this).toJsonString())
        }
    }

    override fun busResult(event: String, msg: BusMsg) {
        if (event == ImagePicker.ALBUM_RESULT) {
            imageResult(msg.stringValue)
        } else if (event == ImagePicker.CROP_RESULT) {
            pickerResult(msg.stringValue)
        }
    }

    private fun imageResult(images: String?) {
        when (ImagePicker.option.cropType) {
            0 -> {
                pickerResult(images)
            }
            1, 2 -> {
                startActivity(Intent(this, CropActivity::class.java).putExtra("images", images))
            }
        }
    }

    private fun pickerResult(images: String?) {
        BusManager.post(ImagePicker.PICKER_RESULT, stringValue = images)
        mBinding.layoutContainer.visibility = View.GONE
        delay(300) {
            finish()
        }
    }
}
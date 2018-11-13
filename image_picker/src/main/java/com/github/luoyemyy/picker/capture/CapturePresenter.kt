package com.github.luoyemyy.picker.capture

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import android.support.v4.content.FileProvider
import android.util.Log
import com.github.luoyemyy.ext.toast
import com.github.luoyemyy.file.FileManager
import com.github.luoyemyy.picker.ImagePicker
import com.github.luoyemyy.picker.R
import java.io.File

class CapturePresenter(app: Application) : AndroidViewModel(app) {

    companion object {
        const val CAPTURE_REQUEST_CODE = 102
    }

    private var mCacheCaptureFile: String? = null

    fun capture(activity: FragmentActivity) {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(activity.packageManager) == null) {
            activity.toast(messageId = R.string.image_picker_need_camera_app)
            Log.e("CapturePresenter", "无相机应用")
            return
        }
        val file = createFile() ?: let {
            activity.toast(messageId = R.string.image_picker_create_file_failure)
            Log.e("CapturePresenter", "创建文件失败")
            return
        }
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(activity, ImagePicker.option.fileProvider, file)
        } else {
            Uri.fromFile(file)
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        activity.startActivityForResult(intent, CAPTURE_REQUEST_CODE)
        mCacheCaptureFile = file.absolutePath
    }

    fun captureResult(context: Context): List<String> {
        val fileName = mCacheCaptureFile ?: return listOf()
        val file = File(fileName)
        MediaStore.Images.Media.insertImage(context.contentResolver, fileName, file.name, null)
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
        return listOf(fileName)
    }

    private fun createFile(): File? {
        val fileName = FileManager.getInstance().getName()
        val file = FileManager.getInstance().outer().publicStandardFile(Environment.DIRECTORY_PICTURES, fileName, FileManager.SUFFIX_IMAGE)
                ?: return null
        if (!file.exists() && file.createNewFile()) {
            return file
        }
        return null
    }
}
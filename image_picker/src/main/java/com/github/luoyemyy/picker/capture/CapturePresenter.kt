package com.github.luoyemyy.picker.capture

import android.Manifest
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
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
import com.github.luoyemyy.permission.PermissionManager
import com.github.luoyemyy.picker.PickerBundle
import com.github.luoyemyy.picker.PickerFragment
import com.github.luoyemyy.picker.R
import java.io.File

class CapturePresenter(app: Application) : AndroidViewModel(app) {

    private var mCacheCaptureFile: String? = null
    val captureErrorFlag = MutableLiveData<Boolean>()

    fun capture(activity: FragmentActivity, pickerBundle: PickerBundle) {
        PermissionManager.withPass {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(activity.packageManager) == null) {
                activity.toast(messageId = R.string.image_picker_need_camera_app)
                captureErrorFlag.value = true
                return@withPass
            }
            val file = createFile(activity) ?: let {
                Log.e("CapturePresenter", "创建文件失败")
                captureErrorFlag.value = true
                return@withPass
            }
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                pickerBundle.fileProvider?.let { FileProvider.getUriForFile(activity, it, file) }
                        ?: throw NullPointerException("need file provider external-path#Pictures dir")
            } else {
                Uri.fromFile(file)
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            activity.startActivityForResult(intent, PickerFragment.CAPTURE_REQUEST_CODE)
            mCacheCaptureFile = file.absolutePath
        }.withDenied { future, _ ->
            future.toSettings(activity, activity.getString(R.string.image_picker_need_camera))
            Log.e("CapturePresenter", "权限不足，需要同时拥有相机和文件读写的权限")
            captureErrorFlag.value = true
        }.request(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    fun captureResult(context: Context): List<String> {
        val fileName = mCacheCaptureFile ?: return listOf()
        val file = File(fileName)
        MediaStore.Images.Media.insertImage(context.contentResolver, fileName, file.name, null)
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
        return listOf(fileName)
    }

    private fun createFile(activity: FragmentActivity): File? {
        FileManager.initManager(activity.application)
        val fileName = FileManager.getInstance().getName()
        val file = FileManager.getInstance().outer().publicStandardFile(Environment.DIRECTORY_PICTURES, fileName, FileManager.SUFFIX_IMAGE)
                ?: return null
        if (!file.exists() && file.createNewFile()) {
            return file
        }
        return null
    }
}
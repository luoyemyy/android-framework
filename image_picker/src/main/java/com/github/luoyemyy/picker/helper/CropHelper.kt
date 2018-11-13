package com.github.luoyemyy.picker.helper

import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import com.github.luoyemyy.file.FileManager
import java.io.File
import java.io.FileOutputStream

object CropHelper {

    fun saveBitmap(bitmap: Bitmap, result: (Boolean, File?) -> Unit) {
        AsyncTask.execute {
            val file = FileManager.getInstance().image()?.apply {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(this))
            }
            Handler(Looper.getMainLooper()).post {
                result(file != null, file)
            }
        }
    }
}
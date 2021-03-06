package com.github.luoyemyy.picker.helper

import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.luoyemyy.file.FileManager
import java.io.FileOutputStream

object ImagePickerHelper {

    @JvmStatic
    @BindingAdapter("image_picker_image_url")
    fun imagePicker(imageView: ImageView, url: String?) {
        Glide.with(imageView).load(url).into(imageView)
    }

    fun saveBitmap(bitmap: Bitmap, result: (Boolean, String?) -> Unit) {
        AsyncTask.execute {
            val file = FileManager.getInstance().image()?.apply {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(this))
            }
            Handler(Looper.getMainLooper()).post {
                result(file != null, file?.absolutePath)
            }
        }
    }
}
package com.github.luoyemyy.picker.helper

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object BindAdapter {
    @JvmStatic
    @BindingAdapter("image_picker_image_url")
    fun imagePicker(imageView: ImageView, url: String?) {
        Glide.with(imageView).load(url).apply(RequestOptions.centerCropTransform()).into(imageView)
    }

    @JvmStatic
    @BindingAdapter("image_picker_image_url_preview")
    fun imagePickerPreview(imageView: ImageView, url: String?) {
        Glide.with(imageView).load(url).into(imageView)
    }

//    fun loadBitmap(url: String): Bitmap {
//        val options = BitmapFactory.Options().apply {
//            inJustDecodeBounds = true
//        }
//        BitmapFactory.decodeFile(url, options)
//
//        var w = options.outWidth
//        var h = options.outHeight
//        var scale = 1
//        do {
//            w /= 2
//            h /= 2
//            scale *= 2
//        } while (w > 300 || h > 300)
//
//        options.inSampleSize = scale
//        options.inJustDecodeBounds = false
//        return BitmapFactory.decodeFile(url, options)
//    }
}
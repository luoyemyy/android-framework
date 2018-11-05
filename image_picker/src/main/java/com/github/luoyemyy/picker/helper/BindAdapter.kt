package com.github.luoyemyy.picker.helper

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object BindAdapter {
    @JvmStatic
    @BindingAdapter("image_picker_image_url")
    fun imagePicker(imageView: ImageView, url: String?) {
        Glide.with(imageView).load(url).into(imageView)
    }
}
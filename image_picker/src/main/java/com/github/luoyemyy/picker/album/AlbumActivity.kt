package com.github.luoyemyy.picker.album

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.luoyemyy.picker.R
import com.github.luoyemyy.picker.databinding.ImagePickerAlbumContainerBinding

class AlbumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ImagePickerAlbumContainerBinding>(this, R.layout.image_picker_album_container)
        supportFragmentManager.beginTransaction()
                .add(R.id.container, AlbumFragment().apply { arguments = intent.extras },"albumFragment")
                .commit()
    }
}
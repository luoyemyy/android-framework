package com.github.luoyemyy.picker.preview

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.github.luoyemyy.mvp.getPresenter
import com.github.luoyemyy.mvp.recycler.AbstractSingleRecyclerAdapter
import com.github.luoyemyy.picker.R
import com.github.luoyemyy.picker.databinding.ImagePickerPreviewBinding
import com.github.luoyemyy.picker.databinding.ImagePickerPreviewRecyclerBinding
import com.github.luoyemyy.picker.entity.Image
import com.github.luoyemyy.picker.helper.BindAdapter

class PreviewActivity : AppCompatActivity() {

    private lateinit var mBinding: ImagePickerPreviewBinding
    private lateinit var mPresenter: PreviewPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.image_picker_preview)

        mBinding.toolbar.apply {
            setNavigationOnClickListener {
                finishAfterTransition()
            }
            inflateMenu(R.menu.image_picker_albun_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                }
                true
            }
        }

        mPresenter = getPresenter()
        mPresenter.setup(this, Adapter())

        mPresenter.liveDataPreviewImage.observe(this, Observer {
            BindAdapter.imagePickerPreview(mBinding.imgPreview, it?.path)
        })

        mBinding.imgPreview.apply {
            setScaleLevels(0.75f, 1f, 3f)

            val shareName = arguments?.getString("shareName")
            val image = arguments?.getString("image")
            BindAdapter.imagePickerPreview(this, image)
            ViewCompat.setTransitionName(this, shareName)
        }

        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        mPresenter.loadInit(arguments)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.image_picker_albun_menu)
        return super.onCreateOptionsMenu(menu)
    }


    inner class Adapter : AbstractSingleRecyclerAdapter<Image, ImagePickerPreviewRecyclerBinding>(mBinding.recyclerView) {

        override fun enableLoadMore(): Boolean {
            return false
        }

        override fun enableEmpty(): Boolean {
            return false
        }

        override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ImagePickerPreviewRecyclerBinding {
            return ImagePickerPreviewRecyclerBinding.inflate(inflater, parent, false)
        }

        override fun bindContentViewHolder(binding: ImagePickerPreviewRecyclerBinding, content: Image, position: Int) {
            binding.image = content
            binding.executePendingBindings()
        }

        override fun onItemClickListener(vh: VH<ImagePickerPreviewRecyclerBinding>, view: View?) {
            mPresenter.clickImage(vh.adapterPosition)
        }

    }
}
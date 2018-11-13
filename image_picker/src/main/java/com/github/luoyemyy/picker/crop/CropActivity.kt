package com.github.luoyemyy.picker.crop

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.pm.ActivityInfo
import android.databinding.DataBindingUtil
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.github.luoyemyy.mvp.getPresenter
import com.github.luoyemyy.mvp.recycler.AbstractSingleRecyclerAdapter
import com.github.luoyemyy.mvp.recycler.VH
import com.github.luoyemyy.picker.R
import com.github.luoyemyy.picker.databinding.ImagePickerCropBinding
import com.github.luoyemyy.picker.databinding.ImagePickerCropRecyclerBinding
import com.github.luoyemyy.picker.entity.CropImage
import com.github.luoyemyy.picker.helper.BindAdapter
import com.github.luoyemyy.picker.helper.CropHelper
import kotlin.math.roundToInt

class CropActivity : AppCompatActivity() {

    private lateinit var mBinding: ImagePickerCropBinding
    private lateinit var mPresenter: CropPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND
        mBinding = DataBindingUtil.setContentView(this, R.layout.image_picker_crop)
        mPresenter = getPresenter()
        mPresenter.setup(this, Adapter())

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.apply {
            setTitle(R.string.image_picker_preview_title)
            setDisplayHomeAsUpEnabled(true)
        }

        mPresenter.liveDataCropImage.observe(this, Observer {
            if (it != null) {
                BindAdapter.imagePicker(mBinding.imgPreview, it.cropPath)
            }
        })
        mPresenter.liveDataSingleImage.observe(this, Observer {
            mBinding.recyclerView.visibility = View.GONE
        })

        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(Decoration(context))
        }
        mPresenter.startCrop(intent.extras)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.image_picker_crop_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
            }
            R.id.reset -> {
                mPresenter.resetCrop()
            }
            R.id.crop -> {
                mBinding.imgPreview.crop {
                    CropHelper.saveBitmap(it) { ok, file ->
                        if (ok && file != null) {
                            mPresenter.crop(file)
                        }
                    }
                }
            }
            R.id.sure -> {
                if (mPresenter.cropResult()) {
                    finishAfterTransition()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class Adapter : AbstractSingleRecyclerAdapter<CropImage, ImagePickerCropRecyclerBinding>(mBinding.recyclerView) {

        override fun enableLoadMore(): Boolean {
            return false
        }

        override fun enableEmpty(): Boolean {
            return false
        }

        override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ImagePickerCropRecyclerBinding {
            return ImagePickerCropRecyclerBinding.inflate(inflater, parent, false)
        }

        override fun bindContentViewHolder(binding: ImagePickerCropRecyclerBinding, content: CropImage, position: Int) {
            binding.image = content
            binding.executePendingBindings()
        }

        override fun onItemClickListener(vh: VH<ImagePickerCropRecyclerBinding>, view: View?) {
            mPresenter.clickImage(vh.adapterPosition)
        }
    }

    class Decoration(context: Context) : RecyclerView.ItemDecoration() {
        private val space = (context.resources.displayMetrics.density * 2).roundToInt()
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.top = space
            outRect.bottom = space
            outRect.right = space
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = space
            }
        }
    }
}
package com.github.luoyemyy.picker.preview

import android.arch.lifecycle.Observer
import android.content.pm.ActivityInfo
import android.databinding.DataBindingUtil
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.Slide
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.*
import com.github.luoyemyy.mvp.getPresenter
import com.github.luoyemyy.mvp.recycler.AbstractSingleRecyclerAdapter
import com.github.luoyemyy.mvp.recycler.VH
import com.github.luoyemyy.picker.R
import com.github.luoyemyy.picker.databinding.ImagePickerPreviewBinding
import com.github.luoyemyy.picker.databinding.ImagePickerPreviewRecyclerBinding
import com.github.luoyemyy.picker.entity.Image
import com.github.luoyemyy.picker.helper.BindAdapter
import com.github.luoyemyy.picker.view.ImageViewListener
import kotlin.math.roundToInt

class PreviewActivity : AppCompatActivity() {

    private lateinit var mBinding: ImagePickerPreviewBinding
    private lateinit var mPresenter: PreviewPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND
        mBinding = DataBindingUtil.setContentView(this, R.layout.image_picker_preview)
        mPresenter = getPresenter()
        mPresenter.setup(this, Adapter())

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.apply {
            setTitle(R.string.image_picker_preview_title)
            setDisplayHomeAsUpEnabled(true)
        }

        mPresenter.liveDataPreviewImage.observe(this, Observer {
            if (it != null) {
                ViewCompat.setTransitionName(mBinding.imgPreview, "${it.index}_share")
                BindAdapter.imagePicker(mBinding.imgPreview, it.path)
            }
        })

        mBinding.imgPreview.apply {
            val shareName = intent.extras?.getString("shareName")
            val path = intent.extras?.getString("image")
            ViewCompat.setTransitionName(this, shareName)
            BindAdapter.imagePicker(this, path)


            addImageViewListener(object : ImageViewListener {
                override fun onChange() {
                    fullScreen(true)
                }

                override fun onSingleTap() {
                    fullScreen(!mPresenter.fullScreen)
                }
            })
        }

        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                val space = (this@PreviewActivity.resources.displayMetrics.density * 2).roundToInt()
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.top = space
                    outRect.bottom = space
                    outRect.right = space
                    if (parent.getChildAdapterPosition(view) == 0) {
                        outRect.left = space
                    }
                }
            })
        }

        mPresenter.loadInit(intent.extras)
        Handler().postDelayed({
            fullScreen(false)
        }, 300)
    }

    private fun fullScreen(fullScreen: Boolean) {
        val visible = if (fullScreen) View.GONE else View.VISIBLE
        TransitionManager.beginDelayedTransition(mBinding.layoutContainer, TransitionSet()
                .addTransition(Slide(Gravity.TOP).addTarget(mBinding.appBarLayout))
                .addTransition(Slide(Gravity.BOTTOM).addTarget(mBinding.recyclerView)))
        mBinding.recyclerView.visibility = visible
        mBinding.appBarLayout.visibility = visible
        mPresenter.fullScreen = fullScreen
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.image_picker_albun_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
            }
            R.id.sure -> {
                mBinding.imgPreview.crop({

                }, {

                })
            }
        }
        return super.onOptionsItemSelected(item)
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

        override fun afterLoadInit(list: List<Image>?) {
            if (list != null) {
                val position = intent.getIntExtra("position", -1)
                if (position in 0 until list.size) {
                    mBinding.recyclerView.scrollToPosition(position)
                }
            }
        }

    }
}
package com.github.luoyemyy.picker.album

import android.app.Activity
import android.app.ActivityOptions
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.ActivityInfo
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Pair
import android.view.*
import android.widget.ImageView
import com.github.luoyemyy.mvp.getPresenter
import com.github.luoyemyy.mvp.recycler.AbstractSingleRecyclerAdapter
import com.github.luoyemyy.mvp.recycler.VH
import com.github.luoyemyy.mvp.recycler.setGridManager
import com.github.luoyemyy.mvp.recycler.setLinearManager
import com.github.luoyemyy.picker.R
import com.github.luoyemyy.picker.databinding.ImagePickerAlbumBinding
import com.github.luoyemyy.picker.databinding.ImagePickerAlbumRecyclerBinding
import com.github.luoyemyy.picker.databinding.ImagePickerBucketRecyclerBinding
import com.github.luoyemyy.picker.entity.Bucket
import com.github.luoyemyy.picker.entity.Image
import com.github.luoyemyy.picker.preview.PreviewActivity

class AlbumActivity : AppCompatActivity() {

    private lateinit var mBinding: ImagePickerAlbumBinding
    private lateinit var mAlbumPresenter: AlbumPresenter
    private lateinit var mBucketPresenter: BucketPresenter
    private lateinit var mBottomSheet: BottomSheetBehavior<RecyclerView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.image_picker_album)

        mBucketPresenter = getPresenter()
        mAlbumPresenter = getPresenter()
        mAlbumPresenter.setup(this, AlbumAdapter())
        mBucketPresenter.setup(this, BucketAdapter())
        if (savedInstanceState != null) {
            mAlbumPresenter.reCalculateImageItemSize()
        }

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.apply {
            setTitle(R.string.image_picker_album_title)
            setDisplayHomeAsUpEnabled(true)
        }
        mAlbumPresenter.setBundle(intent.extras)
        requestedOrientation = if (mAlbumPresenter.pickerBundle.portrait) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        mAlbumPresenter.setMenu()

        mAlbumPresenter.apply {
            liveDataInit.observe(this@AlbumActivity, Observer {
                mBucketPresenter.buckets = mAlbumPresenter.buckets
                mBucketPresenter.loadInit()
            })
            liveDataMenu.observe(this@AlbumActivity, Observer {
                invalidateOptionsMenu()
            })
            liveDataImages.observe(this@AlbumActivity, Observer { images ->
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra("data", images?.map { it.path }?.toTypedArray() ?: arrayOf())
                })
                finishAfterTransition()
            })
        }

        mBinding.recyclerView.apply {
            setGridManager(mAlbumPresenter.getSpan())
            addItemDecoration(AlbumDecoration(mAlbumPresenter.getSpan(), mAlbumPresenter.getSpace()))
        }

        mBinding.recyclerView2.apply {
            setLinearManager()
            mBottomSheet = BottomSheetBehavior.from(this)
            mBottomSheet.isHideable = true
            hideBucket()
        }

        mBinding.fab.setOnClickListener {
            showBucket()
        }

        mAlbumPresenter.loadInit()
    }

    private fun showBucket() {
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        mBinding.fab.hide()
    }

    private fun hideBucket() {
        mBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        mBinding.fab.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.image_picker_albun_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (mAlbumPresenter.mMenuText == null) {
            menu?.findItem(R.id.selected)?.isVisible = false
        } else {
            menu?.findItem(R.id.selected)?.apply {
                isVisible = true
                title = mAlbumPresenter.mMenuText
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
            R.id.sure -> mAlbumPresenter.clickSure()
        }
        return super.onOptionsItemSelected(item)
    }

    inner class AlbumAdapter : AbstractSingleRecyclerAdapter<Image, ImagePickerAlbumRecyclerBinding>(mBinding.recyclerView) {

        override fun enableLoadMore(): Boolean {
            return false
        }

        override fun enableEmpty(): Boolean {
            return false
        }

        override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ImagePickerAlbumRecyclerBinding {
            return ImagePickerAlbumRecyclerBinding.inflate(inflater, parent, false).apply {
                root.layoutParams.width = mAlbumPresenter.getSize()
                root.layoutParams.height = mAlbumPresenter.getSize()
            }
        }

        override fun bindContentViewHolder(binding: ImagePickerAlbumRecyclerBinding, content: Image, position: Int) {
            binding.image = content
            val shareName = "${position}_share"
            ViewCompat.setTransitionName(binding.imageView, shareName)
            binding.executePendingBindings()
        }

        override fun getItemClickViews(binding: ImagePickerAlbumRecyclerBinding): Array<View> {
            return arrayOf(binding.mask, binding.imageView)
        }

        override fun onItemClickListener(vh: VH<ImagePickerAlbumRecyclerBinding>, view: View?) {
            if (view == null) return
            if (view is ImageView) {
                val image = getItem(vh.adapterPosition) ?: return
                val imageView = vh.binding?.imageView ?: return
                val shareName = ViewCompat.getTransitionName(imageView) ?: return

                startActivity(Intent(this@AlbumActivity, PreviewActivity::class.java).apply {
                    putExtra("shareName", shareName)
                    putExtra("image", image.path)
                }, ActivityOptions.makeSceneTransitionAnimation(this@AlbumActivity, Pair(imageView as View, shareName)).toBundle())
            } else {
                mAlbumPresenter.clickSelect(vh.adapterPosition)
            }
        }

        override fun afterLoadRefresh(list: List<Image>?) {
            if (list != null && list.isNotEmpty()) {
                mBinding.recyclerView.scrollToPosition(0)
            }
        }
    }

    inner class BucketAdapter : AbstractSingleRecyclerAdapter<Bucket, ImagePickerBucketRecyclerBinding>(mBinding.recyclerView2) {

        override fun enableEmpty(): Boolean {
            return false
        }

        override fun enableLoadMore(): Boolean {
            return false
        }

        override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ImagePickerBucketRecyclerBinding {
            return ImagePickerBucketRecyclerBinding.inflate(inflater, parent, false)
        }

        override fun bindContentViewHolder(binding: ImagePickerBucketRecyclerBinding, content: Bucket, position: Int) {
            binding.bucket = content
            binding.executePendingBindings()
        }

        override fun onItemClickListener(vh: VH<ImagePickerBucketRecyclerBinding>, view: View?) {
            val item = mBucketPresenter.getDataSet().item(vh.adapterPosition) ?: return
            mAlbumPresenter.bucketId = item.id
            mAlbumPresenter.loadRefresh()
            hideBucket()
        }
    }
}
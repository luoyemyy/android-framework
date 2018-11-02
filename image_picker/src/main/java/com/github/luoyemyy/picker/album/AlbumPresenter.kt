package com.github.luoyemyy.picker.album

import android.app.Application
import android.os.Bundle
import com.github.luoyemyy.framework.ext.toast
import com.github.luoyemyy.framework.mvp.recycler.AbstractRecyclerPresenter
import com.github.luoyemyy.framework.mvp.recycler.LoadType
import com.github.luoyemyy.framework.mvp.recycler.Paging
import com.github.luoyemyy.picker.PickerBundle
import com.github.luoyemyy.picker.R
import com.github.luoyemyy.picker.entity.Image
import kotlin.math.roundToInt

class AlbumPresenter(private val app: Application) : AbstractRecyclerPresenter<Image>(app) {

    var bucketId: Int = 0
    private lateinit var mPickerBundle: PickerBundle
    private lateinit var mBucketPresenter: BucketPresenter

    private val mModel: AlbumModel by lazy { AlbumModel(app) }
    private var mSizePair: Pair<Int, Int>? = null

    fun attachBucket(bucketPresenter: BucketPresenter) {
        mBucketPresenter = bucketPresenter
    }

    override fun beforeLoadInit(bundle: Bundle?) {
        super.beforeLoadInit(bundle)
        mPickerBundle = bundle?.getParcelable("pickerBundle") ?: throw NullPointerException("pickerBundle is null")
    }

    override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<Image>? {
        if (loadType.isInit()){
            val buckets = mModel.queryImage()
            mBucketPresenter.buckets = buckets
            mBucketPresenter.loadInit()
        }
        return mBucketPresenter.buckets?.firstOrNull { it.id == bucketId }?.images
    }

    private fun calculateImageItemSize(): Pair<Int, Int> {
        return mSizePair ?: let {
            val suggestSize = app.resources.displayMetrics.density * 100
            val screenWidth = app.resources.displayMetrics.widthPixels

            val span = (screenWidth / suggestSize).toInt()
            val size = screenWidth / span
            Pair(span, size).apply {
                mSizePair = this
            }
        }
    }

    fun getSpan(): Int {
        return calculateImageItemSize().first
    }

    fun getSize(): Int {
        return calculateImageItemSize().second
    }

    fun getSpace(): Int {
        return (app.resources.displayMetrics.density * 1).roundToInt()
    }

    fun clickSure() {
        val images = findSelectImages()
        if (images.isEmpty()) {
            app.toast(messageId = R.string.image_picker_tip1)
            return
        }


    }

    private fun findSelectImages(): List<Image> {
        return mBucketPresenter.buckets?.firstOrNull()?.images?.filter { it.isChecked } ?: listOf()
    }
}
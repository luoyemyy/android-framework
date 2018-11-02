package com.github.luoyemyy.picker.album

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.github.luoyemyy.framework.mvp.recycler.Paging
import com.github.luoyemyy.framework.mvp.recycler.AbstractRecyclerPresenter
import com.github.luoyemyy.framework.mvp.recycler.LoadType
import com.github.luoyemyy.picker.entity.Bucket

class BucketPresenter(val app: Application) : AbstractRecyclerPresenter<Bucket>(app) {

    var buckets: List<Bucket>? = null
    val liveDataCloseBucket = MutableLiveData<Boolean>()
    private lateinit var mAlbumPresenter: AlbumPresenter

    fun attachAlbum(albumPresenter: AlbumPresenter) {
        mAlbumPresenter = albumPresenter
    }

    override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<Bucket>? {
        return buckets
    }

    fun selectBucket(bucketId: Int) {
        liveDataCloseBucket.value = true
        mAlbumPresenter.bucketId = bucketId
        mAlbumPresenter.loadRefresh()
    }
}
package com.github.luoyemyy.picker.preview

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.github.luoyemyy.ext.toList
import com.github.luoyemyy.mvp.recycler.AbstractRecyclerPresenter
import com.github.luoyemyy.mvp.recycler.LoadType
import com.github.luoyemyy.mvp.recycler.Paging
import com.github.luoyemyy.picker.entity.Image

class PreviewPresenter(app: Application) : AbstractRecyclerPresenter<Image>(app) {

    val liveDataPreviewImage = MutableLiveData<Image>()
    var fullScreen: Boolean = false

    override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<Image>? {
        return bundle?.getString("images")?.toList<Image>()?.apply {
            val position = bundle.getInt("position", -1)
            forEachIndexed { index, image ->
                image.isChecked = index == position
            }
        }
    }

    fun clickImage(position: Int) {
        val oldPosition = getDataSet().dataList().indexOfFirst { it.isChecked }
        if (position != oldPosition) {
            getAdapterSupport()?.getAdapter()?.apply {
                getDataSet().change(oldPosition, this) {
                    it.isChecked = false
                }
                getDataSet().change(position, this) {
                    it.isChecked = true
                    liveDataPreviewImage.postValue(it)
                }
            }
        }
    }
}
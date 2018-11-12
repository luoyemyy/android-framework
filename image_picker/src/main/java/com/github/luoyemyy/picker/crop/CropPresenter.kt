package com.github.luoyemyy.picker.crop

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.github.luoyemyy.ext.toList
import com.github.luoyemyy.mvp.recycler.AbstractRecyclerPresenter
import com.github.luoyemyy.mvp.recycler.LoadType
import com.github.luoyemyy.mvp.recycler.Paging
import com.github.luoyemyy.picker.entity.Image

class CropPresenter(app: Application) : AbstractRecyclerPresenter<Image>(app) {

    val liveDataPreviewImage = MutableLiveData<Image>()

    override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<Image>? {
        val images = bundle?.getString("images")?.toList<String>()
        return images?.mapIndexed { index, s ->
            Image(s, false, index)
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
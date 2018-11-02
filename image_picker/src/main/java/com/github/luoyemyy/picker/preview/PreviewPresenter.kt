package com.github.luoyemyy.picker.preview

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.github.luoyemyy.mvp.recycler.AbstractRecyclerPresenter
import com.github.luoyemyy.mvp.recycler.LoadType
import com.github.luoyemyy.mvp.recycler.Paging
import com.github.luoyemyy.picker.entity.Image

class PreviewPresenter(app: Application) : AbstractRecyclerPresenter<Image>(app) {

    val liveDataPreviewImage = MutableLiveData<Image>()

    override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<Image>? {
        return bundle?.getString("images")?.toList<Image>()?.apply {
            val position = bundle.getInt("position", 0)
            if (isNotEmpty()) {
                this[position].isChecked = true
            }
        }
    }

    private fun previewImage() {
        val image = getDataSet().dataList().firstOrNull { it.isChecked } ?: return
        liveDataPreviewImage.value = image
    }

    fun clickImage(position: Int) {
        val oldPosition = getDataSet().dataList().indexOfFirst { it.isChecked }
        if (position != oldPosition) {
            getDataSet().item(oldPosition)?.isChecked = false
            getAdapterSupport()?.getAdapter()?.notifyItemChanged(oldPosition)
            getDataSet().item(position)?.isChecked = true
            getAdapterSupport()?.getAdapter()?.notifyItemChanged(position)
            previewImage()
        }
    }
}
package com.github.luoyemyy.framework.mvp.recycler

import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.github.luoyemyy.framework.mvp.BasePresenter
import com.github.luoyemyy.framework.mvp.action.IRecyclerPresenter
import com.github.luoyemyy.framework.mvp.action.IRecyclerValue


abstract class AbstractRecyclerPresenter<T>(app: Application) : BasePresenter(app), IRecyclerValue<T>, IRecyclerPresenter {

    private var mLiveData = MutableLiveData<RecyclerData<T>>()

    fun attach(owner: LifecycleOwner, observer: Observer<RecyclerData<T>>) {
        super.attach(owner)
        mLiveData.observe(owner, observer)
    }

    override fun setInitValue(data: List<T>?) {
        mLiveData.value = RecyclerData(RecyclerData.INIT, data)
    }

    override fun setMoreValue(data: List<T>?) {
        mLiveData.value = RecyclerData(RecyclerData.MORE, data)
    }

    override fun setRefreshValue(data: List<T>?) {
        mLiveData.value = RecyclerData(RecyclerData.REFRESH, data)
    }

    override fun setSearchValue(data: List<T>?) {
        mLiveData.value = RecyclerData(RecyclerData.SEARCH, data)
    }

    override fun loadMore() {
    }

    override fun loadRefresh() {
    }

    override fun loadSearch(searchText: String?) {
    }
}
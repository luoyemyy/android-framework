package com.github.luoyemyy.mvp.recycler

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle

abstract class AbstractRecyclerPresenter<T>(app: Application) : AndroidViewModel(app), RecyclerPresenterSupport<T> {

    private lateinit var mDelegate: RecyclerPresenterDelegate<T>

    fun setup(owner: LifecycleOwner, adapter: RecyclerAdapterSupport<T>) {
        adapter.setup(this)
        mDelegate = RecyclerPresenterDelegate(owner, adapter, this)
    }

    override fun getPaging(): Paging {
        return mDelegate.getPaging()
    }

    override fun getAdapterSupport(): RecyclerAdapterSupport<*>? {
        return mDelegate.getAdapterSupport()
    }

    override fun getDataSet(): DataSet<T> {
        return mDelegate.getDataSet()
    }

    override fun loadInit(bundle: Bundle?) {
        mDelegate.loadInit(bundle)
    }

    override fun loadRefresh() {
        mDelegate.loadRefresh()
    }

    override fun loadMore() {
        mDelegate.loadMore()
    }

    override fun loadSearch(search: String) {
        mDelegate.loadSearch(search)
    }
}
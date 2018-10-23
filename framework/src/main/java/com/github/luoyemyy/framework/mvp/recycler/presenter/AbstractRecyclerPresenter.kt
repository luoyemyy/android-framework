package com.github.luoyemyy.framework.mvp.recycler.presenter

import android.app.Application
import android.arch.lifecycle.*
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.github.luoyemyy.framework.async.AsyncRun
import com.github.luoyemyy.framework.mvp.recycler.DataSet
import com.github.luoyemyy.framework.mvp.recycler.Paging
import com.github.luoyemyy.framework.mvp.recycler.adapter.RecyclerAdapterSupport

abstract class AbstractRecyclerPresenter<T>(app: Application) : AndroidViewModel(app), RecyclerPresenterWrapper<T>, RecyclerPresenterSupport<T>, LifecycleObserver {

    private val mDataSet by lazy { DataSet<T>() }
    private val mPaging: Paging by lazy { getPaging() }
    private var mSupport: RecyclerAdapterSupport<T>? = null
    private val mLiveDataRefreshState = MutableLiveData<Boolean>()

    override fun getDataSet(): DataSet<T> {
        return mDataSet
    }

    open fun getPaging(): Paging {
        return Paging.Page()
    }

    fun getAdapterSupport(): RecyclerAdapterSupport<*>? {
        return mSupport
    }


    override fun setup(owner: LifecycleOwner, adapter: RecyclerAdapterSupport<T>) {
        mSupport = adapter
        adapter.setup(this)
        owner.lifecycle.addObserver(this)
        mDataSet.enableEmpty = adapter.enableEmpty()
        mDataSet.enableMore = adapter.enableLoadMore()
        mLiveDataRefreshState.observe(owner, Observer {
            mSupport?.setRefreshState(it ?: false)
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(source: LifecycleOwner?) {
        mSupport = null
        source?.lifecycle?.removeObserver(this)
    }

    override fun afterLoadInit(list: List<T>?) {
        mSupport?.apply {
            mDataSet.initData(list).dispatchUpdatesTo(getAdapter())
            attachToRecyclerView()
        }
    }

    override fun afterLoadRefresh(list: List<T>?) {
        mSupport?.apply {
            mDataSet.setData(list).dispatchUpdatesTo(getAdapter())
        }
    }

    override fun afterLoadMore(list: List<T>?) {
        mSupport?.apply {
            mDataSet.addData(list).dispatchUpdatesTo(getAdapter())
        }
    }

    override fun afterLoadSearch(list: List<T>?) {
        mSupport?.apply {
            mDataSet.setData(list).dispatchUpdatesTo(getAdapter())
        }
    }

    @MainThread
    override fun loadInit(bundle: Bundle?) {
        AsyncRun.newCall<List<T>>()
                .start {
                    beforeLoadInit(bundle)
                    mSupport?.beforeLoadInit(bundle)
                    mPaging.reset()
                    mLiveDataRefreshState.value = true
                }.create {
                    loadData(1, mPaging, bundle)
                }.result {
                    afterLoadInit(it)
                    mSupport?.afterLoadInit(it)
                    mLiveDataRefreshState.value = false
                }
    }

    @MainThread
    override fun loadRefresh() {
        AsyncRun.newCall<List<T>>()
                .start {
                    beforeLoadRefresh()
                    mSupport?.beforeLoadRefresh()
                    mPaging.reset()
                }.create {
                    loadData(2, mPaging)
                }.result {
                    afterLoadRefresh(it)
                    mSupport?.afterLoadRefresh(it)
                    mLiveDataRefreshState.value = false
                }
    }

    @MainThread
    override fun loadMore() {
        if (!mDataSet.canLoadMore()) {
            return
        }
        AsyncRun.newCall<List<T>>()
                .start {
                    beforeLoadMore()
                    mSupport?.beforeLoadMore()
                    mPaging.next()
                }.create {
                    loadData(3, mPaging)
                }.result {
                    afterLoadMore(it)
                    mSupport?.afterLoadMore(it)
                }.error {
                    mPaging.nextError()
                }
    }

    @MainThread
    override fun loadSearch(search: String) {
        AsyncRun.newCall<List<T>>()
                .start {
                    beforeLoadSearch(search)
                    mSupport?.beforeLoadSearch(search)
                    mPaging.reset()
                    mLiveDataRefreshState.value = true
                }.create {
                    loadData(4, mPaging)
                }.result {
                    afterLoadSearch(it)
                    mSupport?.afterLoadSearch(it)
                    mLiveDataRefreshState.value = false
                }
    }

    @WorkerThread
    abstract fun loadData(loadType: Int, paging: Paging, bundle: Bundle? = null): List<T>?
}
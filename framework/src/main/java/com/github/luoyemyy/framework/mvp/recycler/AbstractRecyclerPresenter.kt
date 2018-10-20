package com.github.luoyemyy.framework.mvp.recycler

import android.app.Application
import android.arch.lifecycle.*
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import android.support.v7.widget.RecyclerView
import com.github.luoyemyy.framework.async.AsyncRun

abstract class AbstractRecyclerPresenter<T>(app: Application) : AndroidViewModel(app), IRecyclerPresenter<T>, LifecycleObserver {

    private val mDataSet by lazy { DataSet<T>() }
    private val mPaging: Paging by lazy { getPaging() }
    private var mBridge: RecyclerAdapterBridge<T>? = null

    override fun getDataSet(): DataSet<T> {
        return mDataSet
    }

    open fun getPaging(): Paging {
        return Paging.Page()
    }

    fun getAdapter(): RecyclerView.Adapter<*>? {
        return mBridge?.getAdapter()
    }

    override fun setup(owner: LifecycleOwner, adapterBridge: RecyclerAdapterBridge<T>) {
        mBridge = adapterBridge
        adapterBridge.setup(this)
        owner.lifecycle.addObserver(this)
        mDataSet.enableEmpty = adapterBridge.enableEmpty()
        mDataSet.enableMore = adapterBridge.enableLoadMore()
        mDataSet.refreshStateLiveData.observe(owner, Observer {
            mBridge?.setRefreshState(it ?: false)
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(source: LifecycleOwner?) {
        mBridge = null
        source?.lifecycle?.removeObserver(this)
    }

    @CallSuper
    override fun beforeLoadInit(bundle: Bundle?) {
        mBridge?.beforeLoadInit(bundle)
    }

    @CallSuper
    override fun beforeLoadRefresh() {
        mBridge?.beforeLoadRefresh()
    }

    @CallSuper
    override fun beforeLoadMore() {
        mBridge?.beforeLoadMore()
    }

    @CallSuper
    override fun beforeLoadSearch(search: String) {
        mBridge?.beforeLoadSearch(search)
    }

    @CallSuper
    override fun afterLoadInit(list: List<T>?) {
        mBridge?.apply {
            getAdapter().apply {
                mDataSet.initData(list).dispatchUpdatesTo(this)
            }
            afterLoadInit(list)
            attachToRecyclerView()
        }
    }

    @CallSuper
    override fun afterLoadRefresh(list: List<T>?) {
        mBridge?.apply {
            getAdapter().apply {
                mDataSet.setData(list).dispatchUpdatesTo(this)
            }
            afterLoadRefresh(list)
        }
    }

    @CallSuper
    override fun afterLoadMore(list: List<T>?) {
        mBridge?.apply {
            getAdapter().apply {
                mDataSet.addData(list).dispatchUpdatesTo(this)
            }
            afterLoadMore(list)
        }
    }

    @CallSuper
    override fun afterLoadSearch(list: List<T>?) {
        mBridge?.apply {
            getAdapter().apply {
                mDataSet.setData(list).dispatchUpdatesTo(this)
            }
            afterLoadSearch(list)
        }
    }

    @MainThread
    override fun loadInit(bundle: Bundle?) {
        AsyncRun.newCall<List<T>>()
                .start {
                    beforeLoadInit(bundle)
                    mPaging.reset()
                    mDataSet.notifyRefreshState(true)
                }.create {
                    loadData(1, mPaging)
                }.result { it, _ ->
                    afterLoadInit(it)
                    mDataSet.notifyRefreshState(false)
                }
    }

    @MainThread
    override fun loadRefresh() {
        AsyncRun.newCall<List<T>>()
                .start {
                    beforeLoadRefresh()
                    mPaging.reset()
                }.create {
                    loadData(2, mPaging)
                }.result { it, _ ->
                    afterLoadRefresh(it)
                    mDataSet.notifyRefreshState(false)
                }
    }

    @MainThread
    override fun loadMore() {
        AsyncRun.newCall<List<T>>()
                .start {
                    beforeLoadMore()
                    mPaging.next()
                }.create {
                    loadData(3, mPaging)
                }.result { it, _ ->
                    afterLoadMore(it)
                }
    }

    @MainThread
    override fun loadSearch(search: String) {
        AsyncRun.newCall<List<T>>()
                .start {
                    beforeLoadSearch(search)
                    mPaging.reset()
                    mDataSet.notifyRefreshState(true)
                }.create {
                    loadData(4, mPaging)
                }.result { it, _ ->
                    afterLoadSearch(it)
                    mDataSet.notifyRefreshState(false)
                }
    }

    @WorkerThread
    abstract fun loadData(loadType: Int, paging: Paging): List<T>?
}
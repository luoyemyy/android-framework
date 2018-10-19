package com.github.luoyemyy.framework.mvp.recycler

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.github.luoyemyy.framework.async.AsyncRun

abstract class AbstractRecyclerPresenter<T>(app: Application) : AndroidViewModel(app), IRecyclerPresenter<T> {

    private val mDataSet by lazy { DataSet<T>() }
    private val mPaging: Paging by lazy { getPaging() }

    override fun getDataSet(): DataSet<T> {
        return mDataSet
    }

    open fun getPaging(): Paging {
        return Paging.Page()
    }

    open fun beforeLoadInit(bundle: Bundle?) {}
    open fun beforeLoadRefresh() {}
    open fun beforeLoadMore() {}
    open fun beforeLoadSearch(search: String) {}

    @CallSuper
    open fun afterLoadInit(list: List<T>?) {
        mDataSet.initData(list)
    }

    @CallSuper
    open fun afterLoadRefresh(list: List<T>?) {
        mDataSet.setData(list)
    }

    @CallSuper
    open fun afterLoadMore(list: List<T>?) {
        mDataSet.addData(list)
    }

    @CallSuper
    open fun afterLoadSearch(list: List<T>?) {
        mDataSet.setData(list)
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
        if (!mDataSet.canLoadMore()) {
            return
        }
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
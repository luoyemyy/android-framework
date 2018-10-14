package com.github.luoyemyy.framework.mvp.recycler

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.github.luoyemyy.framework.async.AsyncRun

abstract class AbstractRecyclerPresenter<T>(app: Application) : AndroidViewModel(app), IRecyclerPresenter<T> {

    private val mDataSet by lazy { DataSet<T>() }
    private var page = 1

    override fun getDataSet(): DataSet<T> {
        return mDataSet
    }

    open fun beforeLoadInit(bundle: Bundle?) {}
    open fun beforeLoadRefresh() {}
    open fun beforeLoadMore() {}
    open fun beforeLoadSearch(search: String) {}

    @MainThread
    override fun loadInit(bundle: Bundle?) {
        beforeLoadInit(bundle)
        page = 1
        AsyncRun.newCall<List<T>>()
                .start {
                    mDataSet.notifyRefreshState(true)
                }.create {
                    loadData(page) ?: listOf()
                }.result {
                    mDataSet.initData(it)
                    mDataSet.notifyRefreshState(false)
                }
    }

    @MainThread
    override fun loadRefresh() {
        beforeLoadRefresh()
        page = 1
        AsyncRun.newCall<List<T>>()
                .create {
                    loadData(page) ?: listOf()
                }.result {
                    mDataSet.setData(it)
                    mDataSet.notifyRefreshState(false)
                }
    }

    @MainThread
    override fun loadMore() {
        if (mDataSet.canLoadMore()) {
            beforeLoadMore()
            page++
            AsyncRun.newCall<List<T>>()
                    .create {
                        loadData(page) ?: listOf()
                    }.result {
                        mDataSet.addData(it)
                    }
        }
    }

    @MainThread
    override fun loadSearch(search: String) {
        beforeLoadSearch(search)
        page = 1
        AsyncRun.newCall<List<T>>()
                .start {
                    mDataSet.notifyRefreshState(true)
                }.create {
                    loadData(page) ?: listOf()
                }.result {
                    mDataSet.setData(it)
                    mDataSet.notifyRefreshState(false)
                }
    }

    @WorkerThread
    abstract fun loadData(page: Int): List<T>?
}
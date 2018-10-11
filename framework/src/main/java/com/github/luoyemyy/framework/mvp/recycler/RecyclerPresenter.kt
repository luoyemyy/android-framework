package com.github.luoyemyy.framework.mvp.recycler

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.github.luoyemyy.framework.async.AsyncRun

abstract class RecyclerPresenter<T>(app: Application) : AndroidViewModel(app), IRecyclerPresenter<T> {

    private val mDataSet by lazy { DataSet<T>() }
    private var page = 1

    override fun getDataSet(): DataSet<T> {
        return mDataSet
    }

    open fun beforeLoadInit(bundle: Bundle?) {}
    open fun beforeLoadRefresh() {}
    open fun beforeLoadMore() {}
    open fun beforeLoadSearch(search: String) {}

    private fun run(): AsyncRun.Call<RecyclerResult> {
        return AsyncRun.single().Call(RecyclerResult(false))
    }

    inner class RecyclerResult(override var isSuccess: Boolean = false, var list: List<T>? = null) : AsyncRun.Result

    @MainThread
    override fun loadInit(bundle: Bundle?) {
        beforeLoadInit(bundle)
        page = 1
        run().start {
            mDataSet.notifyRefreshState(true)
            true
        }.create {
            RecyclerResult(true, loadData(page))
        }.success {
            mDataSet.initData(it.list)
            mDataSet.notifyRefreshState(false)
        }
    }

    @MainThread
    override fun loadRefresh() {
        beforeLoadRefresh()
        page = 1
        run().create {
            RecyclerResult(true, loadData(page))
        }.success {
            mDataSet.setData(it.list)
            mDataSet.notifyRefreshState(false)
        }
    }

    @MainThread
    override fun loadMore() {
        if (mDataSet.canLoadMore()) {
            beforeLoadMore()
            page++
            run().create {
                RecyclerResult(true, loadData(page))
            }.success {
                mDataSet.addData(it.list)
            }
        }
    }

    @MainThread
    override fun loadSearch(search: String) {
        beforeLoadSearch(search)
        page = 1
        run().start {
            mDataSet.notifyRefreshState(true)
            true
        }.create {
            RecyclerResult(true, loadData(page))
        }.success {
            mDataSet.setData(it.list)
            mDataSet.notifyRefreshState(false)
        }
    }

    @WorkerThread
    abstract fun loadData(page: Int): List<T>?
}
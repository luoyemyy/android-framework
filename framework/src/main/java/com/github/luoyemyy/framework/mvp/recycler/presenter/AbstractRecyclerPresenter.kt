package com.github.luoyemyy.framework.mvp.recycler.presenter

import android.app.Application
import android.arch.lifecycle.*
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import android.util.Log
import com.github.luoyemyy.framework.async.AsyncRun
import com.github.luoyemyy.framework.mvp.recycler.DataSet
import com.github.luoyemyy.framework.mvp.recycler.Paging
import com.github.luoyemyy.framework.mvp.recycler.adapter.RecyclerAdapterSupport

abstract class AbstractRecyclerPresenter<T>(app: Application) : AndroidViewModel(app), RecyclerPresenterWrapper, RecyclerPresenterSupport<T>, LifecycleObserver {

    private val mDataSet by lazy { DataSet<T>() }
    private val mPaging: Paging by lazy { getPaging() }
    private var mSupport: RecyclerAdapterSupport<T>? = null
    private val mLiveDataRefreshState = MutableLiveData<Boolean>()

    fun setup(owner: LifecycleOwner, adapter: RecyclerAdapterSupport<T>) {
        adapter.setup(this)
        mSupport = adapter
        owner.lifecycle.addObserver(this)
        mDataSet.enableEmpty = adapter.enableEmpty()
        mDataSet.enableMore = adapter.enableLoadMore()
        mLiveDataRefreshState.observe(owner, Observer {
            mSupport?.setRefreshState(it ?: false)
        })
    }

    override fun getPaging(): Paging {
        return Paging.Page()
    }

    final override fun getAdapterSupport(): RecyclerAdapterSupport<*>? {
        return mSupport
    }

    override fun getDataSet(): DataSet<T> {
        return mDataSet
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(source: LifecycleOwner?) {
        mSupport = null
        source?.lifecycle?.removeObserver(this)
    }

    override fun afterLoadInit(list: List<T>?) {
        mSupport?.apply {
            mDataSet.initData(list)
            getAdapter().notifyDataSetChanged()
            attachToRecyclerView()
        }
    }

    override fun afterLoadRefresh(list: List<T>?) {
        mSupport?.apply {
            mDataSet.setData(list)
            getAdapter().notifyDataSetChanged()
        }
    }

    override fun afterLoadMore(list: List<T>?) {
        mSupport?.apply {
            mDataSet.addData(list).dispatchUpdatesTo(getAdapter())
        }
    }

    override fun afterLoadSearch(list: List<T>?) {
        mSupport?.apply {
            mDataSet.setData(list)
            getAdapter().notifyDataSetChanged()
        }
    }

    open fun asyncRun(s: () -> Unit, c: () -> List<T>?, r: (List<T>?) -> Unit, e: (Throwable?) -> Unit = {}) {
        AsyncRun.newCall<List<T>>().start { s() }.create { c() }.result(r).error(e)
    }

    @MainThread
    override fun loadInit(bundle: Bundle?) {
        asyncRun({
            beforeLoadInit(bundle)
            mSupport?.beforeLoadInit(bundle)
            mPaging.reset()
            mLiveDataRefreshState.value = true
        }, {
            loadData(1, mPaging, bundle)
        }, {
            afterLoadInit(it)
            mSupport?.afterLoadInit(it)
            mLiveDataRefreshState.value = false
        })
    }

    @MainThread
    override fun loadRefresh() {
        asyncRun({
            beforeLoadRefresh()
            mSupport?.beforeLoadRefresh()
            mPaging.reset()
        }, {
            loadData(2, mPaging)
        }, {
            afterLoadRefresh(it)
            mSupport?.afterLoadRefresh(it)
            mLiveDataRefreshState.value = false
        })
    }

    @MainThread
    override fun loadMore() {
        if (!mDataSet.canLoadMore()) {
            return
        }
        asyncRun({
            beforeLoadMore()
            mSupport?.beforeLoadMore()
            mPaging.next()
        }, {
            loadData(3, mPaging)
        }, {
            afterLoadMore(it)
            mSupport?.afterLoadMore(it)
        }, {
            mPaging.nextError()
        })
    }

    @MainThread
    override fun loadSearch(search: String) {
        asyncRun({
            beforeLoadSearch(search)
            mSupport?.beforeLoadSearch(search)
            mPaging.reset()
            mLiveDataRefreshState.value = true
        }, {
            loadData(4, mPaging)
        }, {
            afterLoadSearch(it)
            mSupport?.afterLoadSearch(it)
            mLiveDataRefreshState.value = false
        })
    }

    @WorkerThread
    abstract fun loadData(loadType: Int, paging: Paging, bundle: Bundle? = null): List<T>?
}
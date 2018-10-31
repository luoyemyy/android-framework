package com.github.luoyemyy.framework.mvp.recycler

import android.app.Application
import android.arch.lifecycle.*
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.github.luoyemyy.framework.async.AsyncRun

abstract class AbstractRecyclerPresenter<T>(app: Application) : AndroidViewModel(app), RecyclerPresenterWrapper, RecyclerPresenterSupport<T>, LifecycleObserver {

    private val mDataSet by lazy { DataSet<T>() }
    private val mPaging: Paging by lazy { getPaging() }
    private var mAdapterSupport: RecyclerAdapterSupport<T>? = null
    private val mLiveDataRefreshState = MutableLiveData<Boolean>()

    fun setup(owner: LifecycleOwner, adapter: RecyclerAdapterSupport<T>) {
        adapter.setup(this)
        mAdapterSupport = adapter
        owner.lifecycle.addObserver(this)
        mDataSet.enableEmpty = adapter.enableEmpty()
        mDataSet.enableMore = adapter.enableLoadMore()
        mLiveDataRefreshState.observe(owner, Observer {
            mAdapterSupport?.setRefreshState(it ?: false)
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(source: LifecycleOwner?) {
        mAdapterSupport = null
        source?.lifecycle?.removeObserver(this)
    }

    override fun getPaging(): Paging {
        return Paging.Page()
    }

    final override fun getAdapterSupport(): RecyclerAdapterSupport<*>? {
        return mAdapterSupport
    }

    override fun getDataSet(): DataSet<T> {
        return mDataSet
    }

    @CallSuper
    @MainThread
    override fun beforeLoadInit(bundle: Bundle?) {
        mAdapterSupport?.apply {
            beforeLoadInit(bundle)
            mPaging.reset()
            mLiveDataRefreshState.value = true
        }
    }

    @CallSuper
    @MainThread
    override fun loadInit(bundle: Bundle?) {
        AsyncRun.newCall<List<T>>().start {
            beforeLoadInit(bundle)
        }.create {
            loadData(LoadType.init(), mPaging, bundle)
        }.result {
            afterLoadInit(it)
        }
    }

    @CallSuper
    @MainThread
    override fun afterLoadInit(list: List<T>?) {
        mAdapterSupport?.apply {
            mDataSet.initData(list, getAdapter())
            attachToRecyclerView()
            afterLoadInit(list)
            mLiveDataRefreshState.value = false
        }
    }

    @CallSuper
    @MainThread
    override fun beforeLoadRefresh() {
        mAdapterSupport?.apply {
            beforeLoadRefresh()
            mPaging.reset()
        }
    }

    @CallSuper
    @MainThread
    override fun loadRefresh() {
        AsyncRun.newCall<List<T>>().start {
            beforeLoadRefresh()
        }.create {
            loadData(LoadType.refresh(), mPaging)
        }.result {
            afterLoadRefresh(it)
        }
    }

    @CallSuper
    @MainThread
    override fun afterLoadRefresh(list: List<T>?) {
        mAdapterSupport?.apply {
            mDataSet.setData(list, getAdapter())
            afterLoadRefresh(list)
            mLiveDataRefreshState.value = false
        }
    }

    @CallSuper
    @MainThread
    override fun beforeLoadMore() {
        mAdapterSupport?.apply {
            beforeLoadMore()
            mPaging.next()
        }
    }

    @CallSuper
    @MainThread
    override fun loadMore() {
        if (!mDataSet.canLoadMore()) {
            return
        }
        AsyncRun.newCall<List<T>>().start {
            beforeLoadMore()
        }.create {
            loadData(LoadType.more(), mPaging)
        }.result {
            afterLoadMore(it)
        }.error {
            mPaging.nextError()
            mAdapterSupport?.apply {
                mDataSet.addError(getAdapter())
            }
        }
    }

    @CallSuper
    @MainThread
    override fun afterLoadMore(list: List<T>?) {
        mAdapterSupport?.apply {
            mDataSet.addData(list, getAdapter())
            afterLoadMore(list)
        }
    }

    @CallSuper
    @MainThread
    override fun beforeLoadSearch(search: String) {
        mAdapterSupport?.apply {
            beforeLoadSearch(search)
            mPaging.reset()
            mLiveDataRefreshState.value = true
        }
    }

    @CallSuper
    @MainThread
    override fun loadSearch(search: String) {
        AsyncRun.newCall<List<T>>().start {
            beforeLoadSearch(search)
        }.create {
            loadData(LoadType.search(), mPaging, null, search)
        }.result {
            afterLoadSearch(it)
        }
    }

    @CallSuper
    @MainThread
    override fun afterLoadSearch(list: List<T>?) {
        mAdapterSupport?.apply {
            mDataSet.setData(list, getAdapter())
            afterLoadSearch(list)
            mLiveDataRefreshState.value = false
        }
    }

    @CallSuper
    @WorkerThread
    abstract fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle? = null, search: String? = null): List<T>?
}
package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.*
import android.os.Bundle
import com.github.luoyemyy.framework.async.AsyncRun

class RecyclerPresenterDelegate<T>(owner: LifecycleOwner, private var mAdapterSupport: RecyclerAdapterSupport<T>?, private val mPresenterSupport: RecyclerPresenterSupport<T>) : RecyclerPresenterSupport<T>, LifecycleObserver {

    private val mDataSet by lazy { DataSet<T>() }
    private val mPaging: Paging by lazy { getPaging() }
    private val mLiveDataRefreshState = MutableLiveData<Boolean>()

    init {
        owner.lifecycle.addObserver(this)

        mAdapterSupport?.apply {
            mDataSet.enableEmpty = enableEmpty()
            mDataSet.enableMore = enableLoadMore()
            mLiveDataRefreshState.observe(owner, Observer {
                setRefreshState(it ?: false)
            })
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(source: LifecycleOwner?) {
        mAdapterSupport = null
        source?.lifecycle?.removeObserver(this)
    }

    override fun getPaging(): Paging {
        return Paging.Page()
    }

    override fun getAdapterSupport(): RecyclerAdapterSupport<*>? {
        return mAdapterSupport
    }

    override fun getDataSet(): DataSet<T> {
        return mDataSet
    }

    override fun beforeLoadInit(bundle: Bundle?) {
        mPresenterSupport.beforeLoadInit(bundle)
        mAdapterSupport?.apply {
            beforeLoadInit(bundle)
            mPaging.reset()
            mLiveDataRefreshState.value = true
        }
    }

    override fun loadInit(bundle: Bundle?) {
        val loadType = LoadType.init()
        AsyncRun.newCall<List<T>>()
                .start { beforeLoadInit(bundle) }
                .create { loadData(loadType, mPaging, bundle) }
                .result { afterLoadInit(it) }
                .error { loadError(loadType) }
    }

    override fun afterLoadInit(list: List<T>?) {
        mPresenterSupport.afterLoadInit(list)
        mAdapterSupport?.apply {
            mDataSet.initData(list, getAdapter())
            attachToRecyclerView()
            afterLoadInit(list)
            mLiveDataRefreshState.value = false
        }
    }

    override fun beforeLoadRefresh() {
        mPresenterSupport.beforeLoadRefresh()
        mAdapterSupport?.apply {
            beforeLoadRefresh()
            mPaging.reset()
        }
    }

    override fun loadRefresh() {
        val loadType = LoadType.refresh()
        AsyncRun.newCall<List<T>>()
                .start { beforeLoadRefresh() }
                .create { loadData(loadType, mPaging) }
                .result { afterLoadRefresh(it) }
                .error { loadError(loadType) }
    }

    override fun afterLoadRefresh(list: List<T>?) {
        mPresenterSupport.afterLoadRefresh(list)
        mAdapterSupport?.apply {
            mDataSet.setData(list, getAdapter())
            afterLoadRefresh(list)
            mLiveDataRefreshState.value = false
        }
    }

    override fun beforeLoadMore() {
        mPresenterSupport.beforeLoadMore()
        mAdapterSupport?.apply {
            beforeLoadMore()
            mPaging.next()
        }
    }

    override fun loadMore() {
        if (!mDataSet.canLoadMore()) {
            return
        }
        val loadType = LoadType.more()
        AsyncRun.newCall<List<T>>()
                .start { beforeLoadMore() }
                .create { loadData(loadType, mPaging) }
                .result { afterLoadMore(it) }
                .error { loadError(loadType) }
    }

    override fun afterLoadMore(list: List<T>?) {
        mPresenterSupport.afterLoadMore(list)
        mAdapterSupport?.apply {
            mDataSet.addData(list, getAdapter())
            afterLoadMore(list)
        }
    }

    override fun beforeLoadSearch(search: String) {
        mPresenterSupport.beforeLoadSearch(search)
        mAdapterSupport?.apply {
            beforeLoadSearch(search)
            mPaging.reset()
            mLiveDataRefreshState.value = true
        }
    }

    override fun loadSearch(search: String) {
        val loadType = LoadType.search()
        AsyncRun.newCall<List<T>>()
                .start { beforeLoadSearch(search) }
                .create { loadData(loadType, mPaging, null, search) }
                .result { afterLoadSearch(it) }
                .error { loadError(loadType) }
    }

    override fun afterLoadSearch(list: List<T>?) {
        mPresenterSupport.afterLoadSearch(list)
        mAdapterSupport?.apply {
            mDataSet.setData(list, getAdapter())
            afterLoadSearch(list)
            mLiveDataRefreshState.value = false
        }
    }

    override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<T>? {
        return mPresenterSupport.loadData(loadType, paging, bundle, search)
    }

    override fun loadError(loadType: LoadType): Boolean {
        if (!mPresenterSupport.loadError(loadType)) {
            when {
                loadType.isInit() -> afterLoadInit(null)
                loadType.isRefresh() -> afterLoadRefresh(null)
                loadType.isMore() -> {
                    mPaging.nextError()
                    mAdapterSupport?.apply {
                        mDataSet.addError(getAdapter())
                    }
                }
                loadType.isSearch() -> afterLoadSearch(null)
            }
        }
        return true
    }
}
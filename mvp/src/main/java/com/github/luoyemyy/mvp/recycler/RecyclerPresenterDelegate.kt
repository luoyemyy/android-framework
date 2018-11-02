package com.github.luoyemyy.mvp.recycler

import android.annotation.SuppressLint
import android.arch.lifecycle.*
import android.os.Bundle
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RecyclerPresenterDelegate<T>(owner: LifecycleOwner, adapter: RecyclerAdapterSupport<T>, private val mPresenterWrapper: RecyclerPresenterWrapper<T>) : RecyclerPresenterSupport<T>, LifecycleObserver {

    private val mDataSet by lazy { DataSet<T>() }
    private var mPaging: Paging = Paging.Page()
    private var mAdapterSupport: RecyclerAdapterSupport<T>? = adapter
    private val mLiveDataRefreshState = MutableLiveData<Boolean>()

    init {
        owner.lifecycle.addObserver(this)
        adapter.setup(this)

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

    override fun getDataSet(): DataSet<T> {
        return mDataSet
    }

    override fun getPaging(): Paging {
        return mPaging
    }

    fun setPaging(paging: Paging) {
        mPaging = paging
    }

    fun getAdapterSupport(): RecyclerAdapterSupport<*>? {
        return mAdapterSupport
    }

    private fun beforeLoadInit(bundle: Bundle?) {
        mPresenterWrapper.beforeLoadInit(bundle)
        mAdapterSupport?.apply {
            beforeLoadInit(bundle)
            mPaging.reset()
            mLiveDataRefreshState.value = true
        }
    }

    override fun loadInit(bundle: Bundle?) {
        loadData(LoadType.init(), bundle)
    }

    private fun afterLoadInit(list: List<T>?) {
        mPresenterWrapper.afterLoadInit(list)
        mAdapterSupport?.apply {
            mDataSet.initData(list, getAdapter())
            attachToRecyclerView()
            afterLoadInit(list)
            mLiveDataRefreshState.value = false
        }
    }

    private fun beforeLoadRefresh() {
        mPresenterWrapper.beforeLoadRefresh()
        mAdapterSupport?.apply {
            beforeLoadRefresh()
            mPaging.reset()
        }
    }

    override fun loadRefresh() {
        loadData(LoadType.refresh())
    }

    private fun afterLoadRefresh(list: List<T>?) {
        mPresenterWrapper.afterLoadRefresh(list)
        mAdapterSupport?.apply {
            mDataSet.setData(list, getAdapter())
            afterLoadRefresh(list)
            mLiveDataRefreshState.value = false
        }
    }

    private fun beforeLoadMore() {
        mPresenterWrapper.beforeLoadMore()
        mAdapterSupport?.apply {
            beforeLoadMore()
            mPaging.next()
        }
    }

    override fun loadMore() {
        if (!mDataSet.canLoadMore()) {
            return
        }
        loadData(LoadType.more())

    }

    private fun afterLoadMore(list: List<T>?) {
        mPresenterWrapper.afterLoadMore(list)
        mAdapterSupport?.apply {
            mDataSet.addData(list, getAdapter())
            afterLoadMore(list)
        }
    }

    private fun beforeLoadSearch(search: String) {
        mPresenterWrapper.beforeLoadSearch(search)
        mAdapterSupport?.apply {
            beforeLoadSearch(search)
            mPaging.reset()
            mLiveDataRefreshState.value = true
        }
    }

    override fun loadSearch(search: String) {
        loadData(LoadType.search(), null, search)
    }

    private fun afterLoadSearch(list: List<T>?) {
        mPresenterWrapper.afterLoadSearch(list)
        mAdapterSupport?.apply {
            mDataSet.setData(list, getAdapter())
            afterLoadSearch(list)
            mLiveDataRefreshState.value = false
        }
    }

    @SuppressLint("CheckResult")
    private fun loadData(loadType: LoadType, bundle: Bundle? = null, search: String? = null): List<T>? {
        loadBefore(loadType, bundle, search)
        Single
                .create<List<T>> {
                    it.onSuccess(mPresenterWrapper.loadData(loadType, mPaging, bundle, search)
                            ?: listOf())
                }
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loadAfter(loadType, it)
                }, {
                    loadError(loadType)
                })
        return mPresenterWrapper.loadData(loadType, mPaging, bundle, search)
    }

    private fun loadBefore(loadType: LoadType, bundle: Bundle? = null, search: String? = null) {
        when {
            loadType.isInit() -> beforeLoadInit(bundle)
            loadType.isRefresh() -> beforeLoadRefresh()
            loadType.isMore() -> beforeLoadMore()
            loadType.isSearch() -> beforeLoadSearch(search ?: "")
        }
    }

    private fun loadAfter(loadType: LoadType, list: List<T>?) {
        when {
            loadType.isInit() -> afterLoadInit(list)
            loadType.isRefresh() -> afterLoadRefresh(list)
            loadType.isMore() -> afterLoadMore(list)
            loadType.isSearch() -> afterLoadSearch(list)
        }
    }

    private fun loadError(loadType: LoadType) {
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
}
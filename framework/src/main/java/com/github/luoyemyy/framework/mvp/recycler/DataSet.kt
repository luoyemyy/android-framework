package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.MutableLiveData
import android.support.v7.util.DiffUtil

class DataSet<T> {

    companion object {
        const val EMPTY = -1
        const val MORE_LOADING = -2
        const val MORE_END = -3
        const val CONTENT = 1
    }

    /**
     * 额外的数据项（加载更多，无数据）
     */
    private class ExtraItem(var type: Int)

    /**
     * 通知更新item
     */
    internal val diffResultLiveData = MutableLiveData<DiffUtil.DiffResult>()
    /**
     * 通知更新刷新样式
     */
    internal val refreshStateLiveData = MutableLiveData<Boolean>()
    internal var enableEmpty = true
    internal var enableMore = true

    private val mData: MutableList<T> = mutableListOf()

    private val mEmptyItem = ExtraItem(EMPTY)
    private val mMoreLoadingItem = ExtraItem(MORE_LOADING)
    private val mMoreEndItem = ExtraItem(MORE_END)
    /**
     * 加载更多状态
     */
    private var moreLoadingState = false
    /**
     * 标记加载更多没有更多数据
     */
    private var flagMoreEnd = false
    /**
     * 是否加过数据，如果没有加载过数据，则不会出现空数据提示样式
     */
    private var initLoad = false

    fun canLoadMore(): Boolean {
        return if (enableMore && !moreLoadingState && !flagMoreEnd) {
            moreLoadingState = true
            flagMoreEnd = false
            true
        } else {
            false
        }
    }

    fun count(): Int {
        var count = mData.size
        if (count == 0) {
            if (enableEmpty && initLoad) {
                count++
            }
        } else {
            if (enableMore) {
                count++
            }
        }
        return count
    }

    fun type(position: Int): Int {
        val item = itemList()[position]
        return if (item is ExtraItem) {
            item.type
        } else {
            CONTENT
        }
    }


    fun item(position: Int): T? {
        return itemListWithoutExtra()[position]
    }

    private fun itemListWithoutExtra(): List<T?> {
        val list = mutableListOf<T?>()
        if (mData.isEmpty()) {
            if (enableEmpty && initLoad) {
                list.add(null)
            }
        } else {
            mData.forEach {
                list.add(it)
            }
            if (enableMore) {
                list.add(null)
            }
        }
        return list
    }


    private fun itemList(): List<Any?> {
        val list = mutableListOf<Any?>()
        if (mData.isEmpty()) {
            if (enableEmpty && initLoad) {
                list.add(mEmptyItem)
            }
        } else {
            mData.forEach {
                list.add(it)
            }
            if (enableMore) {
                if (flagMoreEnd) {
                    list.add(mMoreEndItem)
                } else {
                    list.add(mMoreLoadingItem)
                }
            }
        }
        return list
    }

    fun dataList(): List<T> = mData

    fun initData(list: List<T>?) {
        setData(list)
    }

    fun setData(list: List<T>?) {
        opData {
            flagMoreEnd = false
            moreLoadingState = false
            initLoad = true
            mData.clear()
            if (list != null && list.isNotEmpty()) {
                mData.addAll(list)
            }
        }
    }

    fun addData(list: List<T>?) {
        opData {
            moreLoadingState = false
            if (list != null && list.isNotEmpty()) {
                mData.addAll(list)
            } else {
                flagMoreEnd = true
            }
        }
    }

    fun remove(list: List<T>?) {
        opData {
            list?.forEach {
                mData.remove(it)
            }
        }
    }

    fun opData(op: () -> Unit) {
        val oldList = itemList()
        op()
        val newList = itemList()
        notifyAdapter(oldList, newList)
    }

    fun notifyRefreshState(refreshing: Boolean) {
        refreshStateLiveData.postValue(refreshing)
    }

    private fun notifyAdapter(oldList: List<Any?>, newList: List<Any?>) {

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun getOldListSize(): Int = oldList.size

            override fun getNewListSize(): Int = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return true
            }
        })
        diffResultLiveData.value = result
    }
}
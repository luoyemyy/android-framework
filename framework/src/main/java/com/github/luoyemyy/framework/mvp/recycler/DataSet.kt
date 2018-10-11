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
     *
     */
    internal val liveData = MutableLiveData<DiffUtil.DiffResult>()
    internal val refreshStateLiveData = MutableLiveData<Boolean>()
    internal var enableEmpty = true
    internal var enableMore = true

    private val mData: MutableList<T> = mutableListOf()

    private val mEmptyItem = ExtraItem(EMPTY)
    private val mMoreLoadingItem = ExtraItem(MORE_LOADING)
    private val mMoreEndItem = ExtraItem(MORE_END)
    private var moreLoading = false
    private var moreEnd = false
    private var firstLoad = false

    fun canLoadMore(): Boolean {
        val enable = enableMore && !moreLoading && !moreEnd
        if (enable) {
            moreLoading = true
            moreEnd = false
        }
        return enable
    }

    fun count(): Int {
        var count = mData.size
        if (count == 0) {
            if (enableEmpty && firstLoad) {
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
            if (enableEmpty && firstLoad) {
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


    private fun itemList(): List<Any> {
        val list = mutableListOf<Any>()
        if (mData.isEmpty()) {
            if (enableEmpty && firstLoad) {
                list.add(mEmptyItem)
            }
        } else {
            mData.forEach {
                list.add(it!!)
            }
            if (enableMore) {
                if (moreEnd) {
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
        opData {
            moreEnd = false
            moreLoading = false
            firstLoad = true
            mData.clear()
            if (list != null) {
                mData.addAll(list)
            }
        }
    }

    fun setData(list: List<T>?) {
        opData {
            moreEnd = false
            moreLoading = false
            mData.clear()
            if (list != null) {
                mData.addAll(list)
            }
        }
    }

    fun addData(list: List<T>?) {
        opData {
            moreLoading = false
            if (list != null) {
                mData.addAll(list)
            } else {
                moreEnd = true
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

    fun setFirstLoad() {
        firstLoad = true
    }

    private fun notifyAdapter(oldList: List<Any>, newList: List<Any>) {

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun getOldListSize(): Int = oldList.size

            override fun getNewListSize(): Int = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = oldList[oldItemPosition]
                val oldType = if (oldItem is ExtraItem) {
                    oldItem.type
                } else {
                    CONTENT
                }
                val newItem = newList[newItemPosition]
                val newType = if (newItem is ExtraItem) {
                    newItem.type
                } else {
                    CONTENT
                }
                return oldType == newType
            }
        })
        liveData.postValue(result)
    }
}
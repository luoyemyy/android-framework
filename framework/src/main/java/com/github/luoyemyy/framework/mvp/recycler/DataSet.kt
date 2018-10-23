@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.luoyemyy.framework.mvp.recycler

import android.support.v7.util.DiffUtil
import com.github.luoyemyy.framework.ext.toJsonString

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

    internal var enableEmpty = true
    internal var enableMore = true

    private val mData: MutableList<T> = mutableListOf()

    private val mEmptyItem = ExtraItem(EMPTY)
    private val mMoreLoadingItem = ExtraItem(MORE_LOADING)
    private val mMoreEndItem = ExtraItem(MORE_END)
    /**
     * 加载更多状态
     */
    private var mMoreLoadingState = false
    /**
     * 标记加载更多没有更多数据
     */
    private var mFlagMoreEnd = false

    fun canLoadMore(): Boolean {
        return if (enableMore && !mMoreLoadingState && !mFlagMoreEnd) {
            loadingMore()
            true
        } else {
            false
        }
    }

    /**
     * 开始加载更多
     */
    fun loadingMore() {
        mMoreLoadingState = true
        mFlagMoreEnd = false
    }

    /**
     * 加载更多结束，无更多数据
     */
    fun loadMoreEnd() {
        mMoreLoadingState = false
        mFlagMoreEnd = true
    }

    /**
     * 加载更多结束
     */
    fun loadMoreCompleted() {
        mMoreLoadingState = false
        mFlagMoreEnd = false
    }

    fun count(): Int {
        var count = mData.size
        if (count == 0) {
            if (enableEmpty) {
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
        return itemListWithoutExtra().let {
            if (position >= 0 && position < it.size) {
                it[position]
            } else {
                null
            }
        }
    }

    private fun itemListWithoutExtra(): List<T?> {
        val list = mutableListOf<T?>()
        if (mData.isEmpty()) {
            if (enableEmpty) {
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
            if (enableEmpty) {
                list.add(mEmptyItem)
            }
        } else {
            mData.forEach {
                list.add(it)
            }
            if (enableMore) {
                if (mFlagMoreEnd) {
                    list.add(mMoreEndItem)
                } else {
                    list.add(mMoreLoadingItem)
                }
            }
        }
        return list
    }

    fun dataList(): List<T> = mData

    fun initData(list: List<T>?): DiffUtil.DiffResult {
        return setData(list)
    }

    fun setData(list: List<T>?): DiffUtil.DiffResult {
        return postData {
            mData.clear()
            if (list != null && list.isNotEmpty()) {
                mData.addAll(list)
            }
            loadMoreCompleted()
        }
    }

    fun addData(list: List<T>?): DiffUtil.DiffResult {
        return postData {
            if (list != null && list.isNotEmpty()) {
                mData.addAll(list)
                loadMoreCompleted()
            } else {
                loadMoreEnd()
            }
        }
    }

    fun remove(list: List<T>?): DiffUtil.DiffResult {
        return postData {
            list?.forEach {
                mData.remove(it)
            }
        }
    }

    fun change(position: Int, change: (value: T) -> Unit): DiffUtil.DiffResult {
        return postData(false, position) {
            item(position)?.let {
                change(it)
            }
        }
    }

    fun postData(post: () -> Unit): DiffUtil.DiffResult {
        return postData(true, 0, post)
    }

    fun postData(skipContent: Boolean, comparePosition: Int = 0, post: () -> Unit): DiffUtil.DiffResult {
        val oldList = itemList()
        post()
        val newList = itemList()
        return diff(oldList, newList, skipContent, comparePosition)
    }

    private fun diff(oldList: List<Any?>, newList: List<Any?>, skipContent: Boolean = true, comparePosition: Int = 0): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun getOldListSize(): Int = oldList.size

            override fun getNewListSize(): Int = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return skipContent || (oldItemPosition == comparePosition
                        && newItemPosition == comparePosition
                        && oldList[oldItemPosition].toJsonString() == newList[newItemPosition].toJsonString())
            }
        })
    }
}
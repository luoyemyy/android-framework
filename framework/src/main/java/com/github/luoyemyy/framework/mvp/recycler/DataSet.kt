@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.luoyemyy.framework.mvp.recycler

import android.support.v7.util.DiffUtil

class DataSet<T> {

    companion object {
        const val EMPTY = -1
        const val MORE_INIT = -100
        const val MORE_LOADING = -101
        const val MORE_END = -102
        const val MORE_COMPLETE = -103
        const val MORE_ERROR = -104
        const val CONTENT = 1
    }

    /**
     * 额外的item（加载更多，无数据）
     */
    private data class ExtraItem(val type: Int)

    internal var enableEmpty = true
    internal var enableMore = true

    /**
     * 内容列表
     */
    private val mData: MutableList<T> = mutableListOf()

    private val mEmptyItem by lazy { ExtraItem(EMPTY) }
    private val mMoreLoadingItem by lazy { ExtraItem(MORE_LOADING) }
    private val mMoreEndItem by lazy { ExtraItem(MORE_END) }
    private val mMoreErrorItem by lazy { ExtraItem(MORE_ERROR) }

    /**
     * 加载更多状态
     * -1 未开始
     * -2 加载中
     * -3 加载结束，还有更多
     * -4 加载结束，无更多
     * -5 加载结束，加载错误
     */
    private var mLoadMoreState = MORE_INIT

    /**
     * 判断是否可以加载更多
     * @return true 会将状态调整为加载中
     */
    fun canLoadMore(): Boolean {
        return if (enableMore && mLoadMoreState in arrayOf(MORE_INIT, MORE_COMPLETE, MORE_ERROR)) {
            loadingMore()
            true
        } else {
            false
        }
    }

    /**
     * 设置加载中状态
     */
    fun loadingMore() {
        mLoadMoreState = MORE_LOADING
    }

    /**
     * 设置加载结束，无更多数据
     */
    fun loadMoreEnd() {
        mLoadMoreState = MORE_END
    }

    /**
     * 设置加载结束，还有数据可加载
     */
    fun loadMoreCompleted() {
        mLoadMoreState = MORE_COMPLETE
    }

    /**
     * 设置加载结束，加载失败
     */
    fun loadMoreError() {
        mLoadMoreState = MORE_ERROR
    }

    /**
     * 统计所有的item数量
     */
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

    /**
     * item 的类型
     */
    fun type(position: Int): Int {
        val item = itemListWithExtra().let {
            if (position in 0 until it.size) {
                it[position]
            } else {
                null
            }
        }
        return when (item) {
            null -> 0
            is ExtraItem -> item.type
            else -> CONTENT
        }
    }

    /**
     * 取出item 如果不是内容类型则为null
     */
    fun item(position: Int): T? {
        return itemList().let {
            if (position in 0 until it.size) {
                it[position]
            } else {
                null
            }
        }
    }

    /**
     * item列表 不包含额外的item
     */
    fun itemList(): List<T?> {
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

    /**
     * item 列表 包含额外的item
     */
    private fun itemListWithExtra(): List<Any?> {
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
                when (mLoadMoreState) {
                    MORE_INIT, MORE_LOADING, MORE_COMPLETE -> list.add(mMoreLoadingItem)
                    MORE_END -> list.add(mMoreEndItem)
                    MORE_ERROR -> list.add(mMoreErrorItem)
                }
            }
        }
        return list
    }

    /**
     * 内容列表
     */
    fun dataList(): List<T> = mData

    /**
     * 初始化内容列表
     */
    fun initData(list: List<T>?) {
        setData(list)
    }

    /**
     * 重置内容列表
     */
    fun setData(list: List<T>?) {
        mData.clear()
        if (list != null && list.isNotEmpty()) {
            mData.addAll(list)
        }
        loadMoreCompleted()
    }

    /**
     * 增加内容列表，返回数据集的变化结果
     */
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

    fun addDataAfter(anchor: T?, list: List<T>?): DiffUtil.DiffResult {
        return postData {
            if (list != null && list.isNotEmpty()) {
                val index = if (anchor == null) {
                    0
                } else {
                    val i = mData.indexOf(anchor)
                    if (i > -1) {
                        i + 1
                    } else {
                        0
                    }
                }
                mData.addAll(index, list)
            }
        }
    }

    /**
     * 增加内容列表，出现错误
     */
    fun addError(): DiffUtil.DiffResult {
        return postData {
            loadMoreError()
        }
    }

    /**
     * 删除内容列表，返回数据集的变化结果
     */
    fun remove(list: List<T>?): DiffUtil.DiffResult {
        return postData {
            list?.forEach {
                mData.remove(it)
            }
        }
    }

    /**
     * 修改某一项数据，返回数据集的变化结果
     */
    fun change(position: Int, change: (value: T) -> Unit): DiffUtil.DiffResult {
        return postData(false, position) {
            item(position)?.let {
                change(it)
            }
        }
    }

    /**
     * 比较数据集前后变化，返回数据集的变化结果
     */
    fun postData(post: () -> Unit): DiffUtil.DiffResult {
        return postData(true, -1, post)
    }

    /**
     * 比较数据集前后变化，返回数据集的变化结果
     */
    fun postData(skipContent: Boolean, changePosition: Int = -1, post: () -> Unit): DiffUtil.DiffResult {
        val oldList = itemListWithExtra()
        post()
        val newList = itemListWithExtra()
        return diff(oldList, newList, skipContent, changePosition)
    }

    private fun diff(oldList: List<Any?>, newList: List<Any?>, skipContent: Boolean = true, changePosition: Int = -1): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun getOldListSize(): Int = oldList.size

            override fun getNewListSize(): Int = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return skipContent || newItemPosition != changePosition
            }
        })
    }
}
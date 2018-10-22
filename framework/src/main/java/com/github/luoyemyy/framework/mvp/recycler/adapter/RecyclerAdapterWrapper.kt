package com.github.luoyemyy.framework.mvp.recycler.adapter

import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.luoyemyy.framework.mvp.recycler.VH

interface RecyclerAdapterWrapper<T, BIND : ViewDataBinding> {

    /**
     * 获得指定位置的数据，如果是加载更多或空数据项，则为null
     */
    fun getItem(position: Int): T?

    /**
     * 绑定数据
     */
    fun bindContentViewHolder(binding: BIND, content: T, position: Int)

    /**
     * 创建内容view
     */
    fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BIND

    /**
     * 获得内容类型id，如果是多类型需要重写该方法
     */
    fun getContentType(position: Int, item: T?): Int

    /**
     * 获得需要绑定点击事件的view
     */
    fun getItemClickViews(binding: BIND): Array<View> {
        return arrayOf(binding.root)
    }

    /**
     * 点击绑定了事件的view的回调方法
     */
    fun onItemClickListener(vh: VH<BIND>, view: View?) {}

    /**
     * 增加绑定除了点击事件之外的其他事件
     */
    fun bindItemEvents(vh: VH<BIND>) {}

    /**
     * 获得加载更多-加载中样式
     */
    fun getMoreLoadingLayout(): Int {
        return 0
    }

    /**
     * 获得加载更多-暂无更多样式
     */
    fun getMoreEndLayout(): Int {
        return 0
    }

    /**
     * 获得空数据样式
     */
    fun getEmptyLayout(): Int {
        return 0
    }
}
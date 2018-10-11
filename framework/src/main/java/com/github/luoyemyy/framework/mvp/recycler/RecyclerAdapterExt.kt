package com.github.luoyemyy.framework.mvp.recycler

import android.databinding.ViewDataBinding
import android.view.View
import android.view.ViewGroup

interface RecyclerAdapterExt<T> {
    /**
     * 增加布局文件，多个类型时使用
     */
    fun addLayout(viewType: Int, layoutId: Int)

    /**
     * 设置单个布局文件，单类型使用
     */
    fun setLayout(layoutId: Int)

    /**
     * 绑定数据
     */
    fun bindContentViewHolder(binding: ViewDataBinding, content: T, position: Int)

    /**
     * 创建内容view
     */
    fun createContentView(parent: ViewGroup, viewType: Int): ViewDataBinding

    /**
     * 如果绑定了item的点击事件，则需要重写该方法，
     */
    fun onItemClickListener(view: View, position: Int)

    /**
     * 获得内容类型id，如果是多类型需要重写该方法
     */
    fun getContentType(position: Int, item: T?): Int

    /**
     * 获得指定项内容实体
     */
    fun getItem(position: Int): T?
}
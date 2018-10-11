package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.LifecycleOwner
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class RecyclerAdapter<T>(owner: LifecycleOwner, presenter: IRecyclerPresenter<T>) : RecyclerView.Adapter<VH>(), RecyclerAdapterOp, RecyclerAdapterExt<T> {

    companion object {
        const val DEFAULT_CONTENT_VIEW_TYPE = DataSet.CONTENT
    }

    /**
     * 辅助类
     */
    private val helper by lazy {
        RecyclerAdapterHelper(owner, presenter, this)
    }

    /**
     * item 布局文件集合
     */
    private val layoutIntArray = SparseIntArray()

    /**
     * 增加布局文件，多个类型时使用
     */
    override fun addLayout(viewType: Int, layoutId: Int) {
        layoutIntArray.put(viewType, layoutId)
    }

    /**
     * 设置单个布局文件，单类型使用
     */
    override fun setLayout(layoutId: Int) {
        layoutIntArray.put(DEFAULT_CONTENT_VIEW_TYPE, layoutId)
    }

    /**
     * 设置加载更多-加载中 布局文件
     */
    override fun setMoreLoadingLayout(layoutId: Int) {
        helper.setMoreLoadingLayout(layoutId)
    }

    /**
     * 设置加载更多-无更多数据 布局文件
     */
    override fun setMoreEndLayout(layoutId: Int) {
        helper.setMoreEndLayout(layoutId)
    }

    /**
     * 设置无数据 布局文件
     */
    override fun setEmptyLayout(layoutId: Int) {
        helper.setEmptyLayout(layoutId)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        helper.onBindViewHolder(holder, position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return helper.onCreateViewHolder(parent, viewType)
    }

    /**
     * 创建内容view
     */
    override fun createContentView(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutIntArray[viewType], parent, false)
    }

    override fun enableBindItemListener(enable: Boolean) {
        helper.enableBindItemListener(enable)
    }

    override fun enableLoadMore(enable: Boolean) {
        helper.enableLoadMore(enable)
    }

    override fun enableEmpty(enable: Boolean) {
        helper.enableEmpty(enable)
    }

    /**
     * 如果绑定了item的点击事件，则需要重写该方法，
     */
    override fun onItemClickListener(view: View, position: Int) {
        //nothing
    }

    override fun getItemViewType(position: Int): Int {
        return helper.getItemViewType(position)
    }

    /**
     * 获得内容类型id，如果是多类型需要重写该方法
     */
    override fun getContentType(position: Int, item: T?): Int {
        return DEFAULT_CONTENT_VIEW_TYPE
    }

    /**
     * 获得指定项内容实体
     */
    override fun getItem(position: Int): T? {
        return helper.getItem(position)
    }

    override fun getItemCount(): Int {
        return helper.getItemCount()
    }

}
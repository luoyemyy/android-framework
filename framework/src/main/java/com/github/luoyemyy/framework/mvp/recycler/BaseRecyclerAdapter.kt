package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.LifecycleOwner
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseRecyclerAdapter<T, BIND : ViewDataBinding>(owner: LifecycleOwner, presenter: IRecyclerPresenter<T>) : RecyclerView.Adapter<VH<BIND>>(), RecyclerAdapterOp {

    /**
     * 辅助类
     */
    private val helper by lazy {
        RecyclerAdapterHelper(owner, presenter, this)
    }

    /**
     * item 布局文件集合
     */
    protected val layoutIntArray = SparseIntArray()

    /**
     * 绑定数据
     */
    abstract fun bindContentViewHolder(binding: BIND, content: T, position: Int)


    /**
     * 获得内容类型id，如果是多类型需要重写该方法
     */
    abstract fun getContentType(position: Int, item: T?): Int

    /**
     * 设置刷新控件样式
     */
    open fun setRefreshState(refreshing: Boolean) {

    }

    /**
     * 是否可以点击item
     */
    override fun enableItemClickListener(enable: Boolean) {
        helper.enableItemClickListener(enable)
    }

    /**
     * 如果绑定了item的点击事件，则需要重写该方法，
     */
    open fun onItemClickListener(view: View, position: Int) {
        //nothing
    }

    /**
     * 是否需要加载更多样式
     */
    override fun enableLoadMore(enable: Boolean) {
        helper.enableLoadMore(enable)
    }

    /**
     * 是否需要空数据样式
     */
    override fun enableEmpty(enable: Boolean) {
        helper.enableEmpty(enable)
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

    override fun onBindViewHolder(holder: VH<BIND>, position: Int) {
        helper.onBindViewHolder(holder, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<BIND> {
        return helper.onCreateViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return helper.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return helper.getItemCount()
    }

    /**
     * 创建内容view
     */
    fun createContentView(parent: ViewGroup, viewType: Int): BIND {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutIntArray[viewType], parent, false)
    }

    /**
     * 获得指定项内容实体
     */
    fun getItem(position: Int): T? {
        return helper.getItem(position)
    }
}
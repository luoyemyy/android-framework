package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.LifecycleOwner
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseRecyclerAdapter<T, BIND : ViewDataBinding>(owner: LifecycleOwner, presenter: IRecyclerPresenter<T>) : RecyclerView.Adapter<VH<BIND>>(), RecyclerAdapterOp, RecyclerAdapterExt<T, BIND> {

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
     *
     */
    override fun bindItemClickListener(enable: Boolean) {
        helper.bindItemClickListener(enable)
    }

    /**
     * 如果绑定了item的点击事件，则需要重写该方法，
     */
    override fun onItemClickListener(view: View, position: Int) {
        //nothing
    }

    override fun enableLoadMore(enable: Boolean) {
        helper.enableLoadMore(enable)
    }

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

    /**
     * 创建内容view
     */
    override fun createContentView(parent: ViewGroup, viewType: Int): BIND {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutIntArray[viewType], parent, false)
    }

    override fun getItemViewType(position: Int): Int {
        return helper.getItemViewType(position)
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


    override fun setRefreshState(refreshing: Boolean) {
        //nothing
    }
}
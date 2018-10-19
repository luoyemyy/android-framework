package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.LifecycleOwner
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

abstract class BaseRecyclerAdapter<T, BIND : ViewDataBinding>(owner: LifecycleOwner, recyclerView: RecyclerView, presenter: IRecyclerPresenter<T>) : RecyclerView.Adapter<VH<BIND>>(), RecyclerAdapterOp<T, BIND> {

    /**
     * 辅助类
     */
    private val delegate by lazy {
        RecyclerAdapterDelegate(owner, recyclerView, this, this, presenter)
    }

    override fun onBindViewHolder(holder: VH<BIND>, position: Int) {
        delegate.onBindViewHolder(holder, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<BIND> {
        return delegate.onCreateViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return delegate.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return delegate.getItemCount()
    }

    override fun attachToRecyclerView() {
        delegate.attachToRecyclerView()
    }

    /**
     * 获得指定项内容实体
     */
    override fun getItem(position: Int): T? {
        return delegate.getItem(position)
    }
}
package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.LifecycleOwner
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

abstract class BaseRecyclerAdapter<T, BIND : ViewDataBinding>(owner: LifecycleOwner, presenter: IRecyclerPresenter<T>) : RecyclerView.Adapter<VH<T, BIND>>(), RecyclerAdapterOp<T, BIND> {

    /**
     * 辅助类
     */
    private val helper by lazy {
        RecyclerAdapterHelper(owner, this, this, presenter)
    }

    override fun onBindViewHolder(holder: VH<T, BIND>, position: Int) {
        helper.onBindViewHolder(holder, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<T, BIND> {
        return helper.onCreateViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return helper.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return helper.getItemCount()
    }

    /**
     * 获得指定项内容实体
     */
    override fun getItem(position: Int): T? {
        return helper.getItem(position)
    }
}
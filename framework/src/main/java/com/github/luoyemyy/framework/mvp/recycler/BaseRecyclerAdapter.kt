package com.github.luoyemyy.framework.mvp.recycler

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

abstract class BaseRecyclerAdapter<T, BIND : ViewDataBinding>(private var mRecyclerView: RecyclerView) : RecyclerView.Adapter<VH<BIND>>(), RecyclerAdapterExt<T, BIND>, RecyclerAdapterBridge<T> {

    /**
     * 辅助类
     */
    private lateinit var delegate: RecyclerAdapterDelegate<T, BIND>

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

    /**
     * 获得指定项内容实体
     */
    override fun getItem(position: Int): T? {
        return delegate.getItem(position)
    }

    override fun setup(presenter: IRecyclerPresenter<T>) {
        delegate = RecyclerAdapterDelegate(this, presenter)
    }

    override fun getAdapter(): RecyclerView.Adapter<*> {
        return this
    }

    override fun attachToRecyclerView() {
        if (mRecyclerView.adapter == null) {
            mRecyclerView.adapter = this
        }
    }
}
package com.github.luoyemyy.framework.mvp.recycler.adapter

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.github.luoyemyy.framework.mvp.recycler.*
import com.github.luoyemyy.framework.mvp.recycler.presenter.RecyclerPresenterSupport

abstract class BaseRecyclerAdapter<T, BIND : ViewDataBinding>(private var mRecyclerView: RecyclerView) : RecyclerView.Adapter<VH<BIND>>(), RecyclerAdapterWrapper<T, BIND>, RecyclerAdapterSupport<T> {

    /**
     * 辅助类
     */
    private lateinit var delegate: RecyclerAdapterDelegate<T, BIND>

    override fun setup(presenterSupport: RecyclerPresenterSupport<T>) {
        delegate = RecyclerAdapterDelegate(this, presenterSupport)
    }

    /**
     * 获得指定项内容实体
     */
    override fun getItem(position: Int): T? {
        return delegate.getItem(position)
    }

    override fun getAdapter(): RecyclerView.Adapter<*> {
        return this
    }

    override fun attachToRecyclerView() {
        mRecyclerView.adapter = this
    }

    override fun onBindViewHolder(holder: VH<BIND>, position: Int) {
        delegate.onBindViewHolder(holder, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<BIND> {
        return delegate.onCreateViewHolder(parent, viewType)
    }

    /**
     * @return 如果没有匹配的类型则返回0 内容视图的类型必须大于0
     */
    override fun getItemViewType(position: Int): Int {
        return delegate.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return delegate.getItemCount()
    }
}
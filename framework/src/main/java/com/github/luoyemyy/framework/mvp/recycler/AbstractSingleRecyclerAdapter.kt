package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.LifecycleOwner
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

abstract class AbstractSingleRecyclerAdapter<T, BIND : ViewDataBinding>(owner: LifecycleOwner, recyclerView: RecyclerView, presenter: IRecyclerPresenter<T>) : BaseRecyclerAdapter<T, BIND>(owner, recyclerView, presenter) {

    override fun getContentType(position: Int, item: T?): Int {
        return DataSet.CONTENT
    }
}
package com.github.luoyemyy.framework.mvp.recycler

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

abstract class AbstractSingleRecyclerAdapter<T, BIND : ViewDataBinding>(recyclerView: RecyclerView) : BaseRecyclerAdapter<T, BIND>(recyclerView) {

    override fun getContentType(position: Int, item: T?): Int {
        return DataSet.CONTENT
    }
}
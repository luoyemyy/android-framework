package com.github.luoyemyy.framework.mvp.recycler

import android.databinding.ViewDataBinding

abstract class AbstractSingleRecyclerAdapter<T, BIND : ViewDataBinding> : BaseRecyclerAdapter<T, BIND>() {

    override fun getContentType(position: Int, item: T?): Int {
        return DataSet.CONTENT
    }
}
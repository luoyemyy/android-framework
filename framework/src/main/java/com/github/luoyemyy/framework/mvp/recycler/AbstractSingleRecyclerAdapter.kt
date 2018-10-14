package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.LifecycleOwner
import android.databinding.ViewDataBinding

abstract class AbstractSingleRecyclerAdapter<T, BIND : ViewDataBinding>(owner: LifecycleOwner, presenter: IRecyclerPresenter<T>) : BaseRecyclerAdapter<T, BIND>(owner, presenter) {

    override fun getContentType(position: Int, item: T?): Int {
        return DataSet.CONTENT
    }


}
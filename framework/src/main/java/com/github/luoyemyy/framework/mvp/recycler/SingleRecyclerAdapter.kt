package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.LifecycleOwner
import android.databinding.ViewDataBinding

abstract class SingleRecyclerAdapter<T, BIND : ViewDataBinding>(owner: LifecycleOwner, presenter: IRecyclerPresenter<T>) : BaseRecyclerAdapter<T, BIND>(owner, presenter) {

    companion object {
        const val DEFAULT_CONTENT_VIEW_TYPE = DataSet.CONTENT
    }

    /**
     * 设置单个布局文件，单类型使用
     */
    fun setLayout(layoutId: Int) {
        layoutIntArray.put(DEFAULT_CONTENT_VIEW_TYPE, layoutId)
    }

    /**
     * 获得内容类型id，如果是多类型需要重写该方法
     */
    override fun getContentType(position: Int, item: T?): Int {
        return DEFAULT_CONTENT_VIEW_TYPE
    }

}
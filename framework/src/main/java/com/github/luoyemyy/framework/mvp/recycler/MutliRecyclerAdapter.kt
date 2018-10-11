package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.LifecycleOwner
import android.databinding.ViewDataBinding

abstract class MutliRecyclerAdapter<T>(owner: LifecycleOwner, presenter: IRecyclerPresenter<T>) : BaseRecyclerAdapter<T, ViewDataBinding>(owner, presenter) {

    /**
     * 增加布局文件，多个类型时使用
     */
     fun addLayout(viewType: Int, layoutId: Int) {
        layoutIntArray.put(viewType, layoutId)
    }

}
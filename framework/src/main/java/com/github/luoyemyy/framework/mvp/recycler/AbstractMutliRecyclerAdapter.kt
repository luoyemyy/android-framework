package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.LifecycleOwner
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

abstract class AbstractMutliRecyclerAdapter(owner: LifecycleOwner,recyclerView: RecyclerView, presenter: IRecyclerPresenter<Any>) : BaseRecyclerAdapter<Any, ViewDataBinding>(owner,recyclerView, presenter)
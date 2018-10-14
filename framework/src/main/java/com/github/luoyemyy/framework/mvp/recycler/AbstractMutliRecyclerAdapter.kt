package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.LifecycleOwner
import android.databinding.ViewDataBinding

abstract class AbstractMutliRecyclerAdapter(owner: LifecycleOwner, presenter: IRecyclerPresenter<Any>) : BaseRecyclerAdapter<Any, ViewDataBinding>(owner, presenter)
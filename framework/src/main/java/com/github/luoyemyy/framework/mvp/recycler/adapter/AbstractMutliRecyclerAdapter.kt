package com.github.luoyemyy.framework.mvp.recycler.adapter

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

abstract class AbstractMutliRecyclerAdapter(recyclerView: RecyclerView) : BaseRecyclerAdapter<Any, ViewDataBinding>(recyclerView)
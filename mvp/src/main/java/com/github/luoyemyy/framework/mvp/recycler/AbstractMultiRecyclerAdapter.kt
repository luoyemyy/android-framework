package com.github.luoyemyy.framework.mvp.recycler

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

abstract class AbstractMultiRecyclerAdapter(recyclerView: RecyclerView) : BaseRecyclerAdapter<Any, ViewDataBinding>(recyclerView)
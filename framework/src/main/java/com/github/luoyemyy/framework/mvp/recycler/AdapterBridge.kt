package com.github.luoyemyy.framework.mvp.recycler

import android.support.v7.widget.RecyclerView

interface AdapterBridge {

    fun getAdapter(): RecyclerView.Adapter<*>

    fun attachToRecyclerView()
}
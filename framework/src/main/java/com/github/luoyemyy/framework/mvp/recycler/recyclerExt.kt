package com.github.luoyemyy.framework.mvp.recycler

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

fun RecyclerView.wrap(recyclerAdapter: BaseRecyclerAdapter<*, *>) {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    adapter = recyclerAdapter
}

fun SwipeRefreshLayout.wrap(presenter: RecyclerPresenter<*>) {
    setOnRefreshListener {
        presenter.loadRefresh()
    }
}
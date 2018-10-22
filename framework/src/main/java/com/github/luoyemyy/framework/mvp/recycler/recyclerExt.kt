package com.github.luoyemyy.framework.mvp.recycler

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import com.github.luoyemyy.framework.mvp.recycler.presenter.AbstractRecyclerPresenter

fun RecyclerView.setLinearManager() {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
}

fun RecyclerView.setGridManager(span: Int) {
    layoutManager = StaggeredGridLayoutManager(span, StaggeredGridLayoutManager.VERTICAL)
}

fun SwipeRefreshLayout.wrap(presenter: AbstractRecyclerPresenter<*>) {
    setOnRefreshListener {
        presenter.loadRefresh()
    }
}
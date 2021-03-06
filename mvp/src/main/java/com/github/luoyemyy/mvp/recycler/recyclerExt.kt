package com.github.luoyemyy.mvp.recycler

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager

fun RecyclerView.setLinearManager() {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
}

fun RecyclerView.setHorizontalManager() {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
}

fun RecyclerView.setGridManager(span: Int) {
    layoutManager = StaggeredGridLayoutManager(span, StaggeredGridLayoutManager.VERTICAL)
}

fun SwipeRefreshLayout.wrap(presenter: RecyclerPresenterSupport<*>) {
    setOnRefreshListener {
        presenter.loadRefresh()
    }
}
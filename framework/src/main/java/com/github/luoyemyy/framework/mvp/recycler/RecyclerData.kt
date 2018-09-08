package com.github.luoyemyy.framework.mvp.recycler

class RecyclerData<T>(var type: Int, var data: List<T>?) {

    val isInit = type == INIT
    val isMore = type == MORE
    val isRefresh = type == REFRESH
    val isSearch = type == SEARCH

    companion object {
        const val INIT = 1
        const val MORE = 2
        const val REFRESH = 3
        const val SEARCH = 4
    }

}
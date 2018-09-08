package com.github.luoyemyy.framework.mvp.action

import android.os.Bundle

interface IRecyclerPresenter {
    fun loadInit(bundle: Bundle? = null)
    fun loadMore()
    fun loadRefresh()
    fun loadSearch(searchText: String? = null)
}
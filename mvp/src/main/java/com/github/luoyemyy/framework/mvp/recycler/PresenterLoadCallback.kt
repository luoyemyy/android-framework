package com.github.luoyemyy.framework.mvp.recycler

interface PresenterLoadCallback<T>:LoadCallback<T> {

    fun loadError(loadType: LoadType): Boolean {
        return false
    }
}
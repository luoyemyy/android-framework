package com.github.luoyemyy.mvp.recycler

interface PresenterLoadCallback<T>:LoadCallback<T> {

    fun loadError(loadType: LoadType): Boolean {
        return false
    }
}
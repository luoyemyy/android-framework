package com.github.luoyemyy.framework.mvp.action

interface IRecyclerValue<T> {
    fun setInitValue(data: List<T>?)
    fun setMoreValue(data: List<T>?)
    fun setRefreshValue(data: List<T>?)
    fun setSearchValue(data: List<T>?)
}
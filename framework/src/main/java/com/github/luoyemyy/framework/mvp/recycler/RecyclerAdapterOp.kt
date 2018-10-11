package com.github.luoyemyy.framework.mvp.recycler

interface RecyclerAdapterOp {

    /**
     * 设置加载更多-加载中 布局文件
     */
    fun setMoreLoadingLayout(layoutId: Int)

    /**
     * 设置加载更多-无更多数据 布局文件
     */
    fun setMoreEndLayout(layoutId: Int)

    /**
     * 设置无数据 布局文件
     */
    fun setEmptyLayout(layoutId: Int)

    /**
     * 是否绑定点击item的事件
     */
    fun enableBindItemListener(enable: Boolean = true)

    /**
     * 是否开启加载更过
     */
    fun enableLoadMore(enable: Boolean = true)

    /**
     * 是否显示空数据提示
     */
    fun enableEmpty(enable: Boolean = true)
}
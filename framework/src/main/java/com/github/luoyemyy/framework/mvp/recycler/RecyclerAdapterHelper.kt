package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.github.luoyemyy.framework.ext.dp2px

class RecyclerAdapterHelper<T>(owner: LifecycleOwner, var presenter: IRecyclerPresenter<T>, var adapter: RecyclerAdapter<T>) : RecyclerAdapterOp {

    init {
        presenter.getDataSet().liveData.observe(owner, Observer {
            it?.dispatchUpdatesTo(adapter)
            enableEmpty(true)
        })
    }

    private var mFlagBindItemClick = false
    private var mMoreLoadingLayoutId = 0
    private var mMoreEndLayoutId = 0
    private var mEmptyLayoutId = 0

    override fun enableBindItemListener(enable: Boolean) {
        mFlagBindItemClick = enable
    }

    override fun enableLoadMore(enable: Boolean) {
        presenter.getDataSet().enableMore = enable
    }

    override fun enableEmpty(enable: Boolean) {
        presenter.getDataSet().enableEmpty = enable
    }

    fun onBindViewHolder(holder: VH, position: Int) {
        val type = presenter.getDataSet().type(position)
        if (isContentByType(type)) {
            val item = getItem(position) ?: return
            val binding = holder.binding ?: return
            adapter.bindContentViewHolder(binding, item, position)
        }
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return if (isContentByType(viewType)) {
            val binding = adapter.createContentView(parent, viewType)
            VH(binding, binding.root).apply {
                if (mFlagBindItemClick) {
                    itemView.setOnClickListener {
                        adapter.onItemClickListener(it, adapterPosition)
                    }
                }
            }
        } else {
            VH(null, createExtraView(parent, viewType))
        }
    }

    fun getItemViewType(position: Int): Int {
        val type = presenter.getDataSet().type(position)
        return if (isContentByType(type)) {
            adapter.getContentType(position, getItem(position))
        } else {
            type
        }
    }

    fun getItemCount(): Int = presenter.getDataSet().count()

    fun getItem(position: Int): T? {
        if (position > getItemCount() - 3) {
            Handler().post {
                //                presenter.loadMore()
            }
        }
        return presenter.getDataSet().item(position)
    }

    private fun isContentByType(type: Int): Boolean = type > 0

    private fun createExtraView(parent: ViewGroup, viewType: Int): View {
        return when (viewType) {
            DataSet.EMPTY -> createEmptyView(parent.context)
            DataSet.MORE_LOADING -> createMoreLoadingView(parent.context)
            DataSet.MORE_END -> createMoreEndView(parent.context)
            else -> View(parent.context)
        }
    }

    /**
     * 设置加载更多-加载中 布局文件
     */
    override fun setMoreLoadingLayout(layoutId: Int) {
        mMoreLoadingLayoutId = layoutId
    }

    /**
     * 设置加载更多-无更多数据 布局文件
     */
    override fun setMoreEndLayout(layoutId: Int) {
        mMoreEndLayoutId = layoutId
    }

    /**
     * 设置无数据 布局文件
     */
    override fun setEmptyLayout(layoutId: Int) {
        mEmptyLayoutId = layoutId
    }

    private fun createLayout(context: Context, text: String): LinearLayout {
        val padding = context.dp2px(8)
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.HORIZONTAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(padding, padding, padding, padding)
        val textView = TextView(context)
        textView.text = text
        layout.addView(textView)
        layout.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        return layout
    }

    private fun createEmptyView(context: Context): View {
        return if (mEmptyLayoutId == 0) {
            createLayout(context, "暂无数据")
        } else {
            LayoutInflater.from(context).inflate(mEmptyLayoutId, null)
        }
    }

    private fun createMoreEndView(context: Context): View {
        return if (mMoreEndLayoutId == 0) {
            createLayout(context, "暂无更多")
        } else {
            LayoutInflater.from(context).inflate(mMoreEndLayoutId, null)
        }
    }

    private fun createMoreLoadingView(context: Context): View {
        return if (mMoreLoadingLayoutId == 0) {
            val layout = createLayout(context, "加载中...")
            val padding = context.dp2px(8)
            val progressSize = context.dp2px(20)
            val process = ProgressBar(context)
            layout.addView(process, 0, LinearLayout.LayoutParams(progressSize, progressSize).apply { marginEnd = padding })
            layout
        } else {
            LayoutInflater.from(context).inflate(mMoreLoadingLayoutId, null)
        }
    }
}
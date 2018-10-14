package com.github.luoyemyy.framework.mvp.recycler

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.ViewDataBinding
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

class RecyclerAdapterHelper<T, BIND : ViewDataBinding>(owner: LifecycleOwner, adapter: RecyclerView.Adapter<VH<T, BIND>>, private var op: RecyclerAdapterOp<T, BIND>, private var presenter: IRecyclerPresenter<T>) {

    init {
        presenter.getDataSet().enableMore = op.enableLoadMore()
        presenter.getDataSet().enableEmpty = op.enableEmpty()

        presenter.getDataSet().diffResultLiveData.observe(owner, Observer {
            it?.dispatchUpdatesTo(adapter)
        })
        presenter.getDataSet().refreshStateLiveData.observe(owner, Observer {
            op.setRefreshState(it ?: false)
        })
    }

    fun onBindViewHolder(holder: VH<T, BIND>, position: Int) {
        val type = presenter.getDataSet().type(position)
        if (isContentByType(type)) {
            val item = getItem(position) ?: return
            val binding = holder.binding ?: return
            op.bindContentViewHolder(binding, item, position)
        }
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<T, BIND> {
        return if (isContentByType(viewType)) {
            val binding = op.createContentView(LayoutInflater.from(parent.context), parent, viewType)
            VH(op, binding, binding.root).apply {
                op.getItemClickViews(binding).forEach {
                    it.setOnClickListener(this)
                }
            }
        } else {
            VH(op, null, createExtraView(parent, viewType))
        }
    }

    fun getItemViewType(position: Int): Int {
        val type = presenter.getDataSet().type(position)
        return if (isContentByType(type)) {
            op.getContentType(position, getItem(position))
        } else {
            type
        }
    }

    fun getItemCount(): Int = presenter.getDataSet().count()

    fun getItem(position: Int): T? {
        if (position > getItemCount() - 3) {
            Handler().post {
                presenter.loadMore()
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

    private fun createLayout(context: Context, text: String): LinearLayout {
        val padding = context.dp2px(16)
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
        return if (op.getEmptyLayout() == 0) {
            createLayout(context, "暂无数据")
        } else {
            LayoutInflater.from(context).inflate(op.getEmptyLayout(), null)
        }
    }

    private fun createMoreEndView(context: Context): View {
        return if (op.getMoreEndLayout() == 0) {
            createLayout(context, "暂无更多")
        } else {
            LayoutInflater.from(context).inflate(op.getMoreEndLayout(), null)
        }
    }

    private fun createMoreLoadingView(context: Context): View {
        return if (op.getMoreLoadingLayout() == 0) {
            val layout = createLayout(context, "加载中...")
            val padding = context.dp2px(8)
            val progressSize = context.dp2px(20)
            val process = ProgressBar(context)
            layout.addView(process, 0, LinearLayout.LayoutParams(progressSize, progressSize).apply { marginEnd = padding })
            layout
        } else {
            LayoutInflater.from(context).inflate(op.getMoreLoadingLayout(), null)
        }
    }
}
package com.github.luoyemyy.framework.mvp.recycler.adapter

import android.content.Context
import android.databinding.ViewDataBinding
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.github.luoyemyy.framework.mvp.recycler.DataSet
import com.github.luoyemyy.framework.mvp.recycler.VH
import com.github.luoyemyy.framework.mvp.recycler.presenter.RecyclerPresenterSupport

internal class RecyclerAdapterDelegate<T, BIND : ViewDataBinding>(private var mWrapper: RecyclerAdapterWrapper<T, BIND>, private var mPresenter: RecyclerPresenterSupport<T>) {

//    private val mItemTouchHelper = ItemTouchHelper(object :ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,){
//        override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
//
//        }
//
//        override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
//
//        }
//
//    })
//
    fun onBindViewHolder(holder: VH<BIND>, position: Int) {
        val type = mPresenter.getDataSet().type(position)
        if (isContentByType(type)) {
            val item = getItem(position) ?: return
            val binding = holder.binding ?: return
            mWrapper.bindContentViewHolder(binding, item, position)
        }
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<BIND> {
        return if (isContentByType(viewType)) {
            val binding = mWrapper.createContentView(LayoutInflater.from(parent.context), parent, viewType)
            VH(binding, binding.root).apply {
                mWrapper.bindItemEvents(this)
                mWrapper.getItemClickViews(binding).forEach {
                    it.setOnClickListener { v ->
                        mWrapper.onItemClickListener(this, v)
                    }
                }
            }
        } else {
            VH(null, createExtraView(parent, viewType))
        }
    }

    fun getItemViewType(position: Int): Int {
        val type = mPresenter.getDataSet().type(position)
        return if (isContentByType(type)) {
            mWrapper.getContentType(position, getItem(position))
        } else {
            type
        }
    }

    fun getItemCount(): Int {
        return mPresenter.getDataSet().count()
    }

    fun getItem(position: Int): T? {
        if (position > getItemCount() - 3) {
            Handler().post {
                mPresenter.loadMore()
            }
        }
        return mPresenter.getDataSet().item(position)
    }

    private fun isContentByType(type: Int): Boolean {
        return type > 0
    }

    private fun createExtraView(parent: ViewGroup, viewType: Int): View {
        return when (viewType) {
            DataSet.EMPTY -> createEmptyView(parent.context)
            DataSet.MORE_LOADING -> createMoreLoadingView(parent.context)
            DataSet.MORE_END -> createMoreEndView(parent.context)
            else -> View(parent.context)
        }
    }

    private fun dp2px(context: Context, dp: Int): Int {
        return Math.round(context.resources.displayMetrics.density * dp)
    }

    private fun createLayout(context: Context, text: String): LinearLayout {
        val padding = dp2px(context, 16)
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
        return if (mWrapper.getEmptyLayout() == 0) {
            createLayout(context, "暂无数据")
        } else {
            LayoutInflater.from(context).inflate(mWrapper.getEmptyLayout(), null)
        }
    }

    private fun createMoreEndView(context: Context): View {
        return if (mWrapper.getMoreEndLayout() == 0) {
            createLayout(context, "暂无更多")
        } else {
            LayoutInflater.from(context).inflate(mWrapper.getMoreEndLayout(), null)
        }
    }

    private fun createMoreLoadingView(context: Context): View {
        return if (mWrapper.getMoreLoadingLayout() == 0) {
            val layout = createLayout(context, "加载中...")
            val padding = dp2px(context, 8)
            val progressSize = dp2px(context, 20)
            val process = ProgressBar(context)
            layout.addView(process, 0, LinearLayout.LayoutParams(progressSize, progressSize).apply { marginEnd = padding })
            layout
        } else {
            LayoutInflater.from(context).inflate(mWrapper.getMoreLoadingLayout(), null)
        }
    }
}
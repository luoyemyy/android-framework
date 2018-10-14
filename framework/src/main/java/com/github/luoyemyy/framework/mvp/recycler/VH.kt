package com.github.luoyemyy.framework.mvp.recycler

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.View

class VH<T, BIND : ViewDataBinding>(var op: RecyclerAdapterOp<T, BIND>, var binding: BIND?, view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
    override fun onClick(v: View?) {
        op.onItemClickListener(binding, v, adapterPosition)
    }
}

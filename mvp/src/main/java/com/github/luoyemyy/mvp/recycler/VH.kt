package com.github.luoyemyy.mvp.recycler

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.View

class VH<BIND : ViewDataBinding>(var binding: BIND?, view: View) : RecyclerView.ViewHolder(view)

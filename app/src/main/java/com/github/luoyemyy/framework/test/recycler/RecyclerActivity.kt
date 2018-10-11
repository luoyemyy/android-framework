package com.github.luoyemyy.framework.test.recycler

import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.github.luoyemyy.framework.ext.getPresenter
import com.github.luoyemyy.framework.mvp.recycler.RecyclerAdapter
import com.github.luoyemyy.framework.mvp.recycler.RecyclerPresenter
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityRecyclerBinding
import com.github.luoyemyy.framework.test.databinding.ActivityRecyclerRecyclerBinding

class RecyclerActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRecyclerBinding
    private lateinit var mPresenter: Presenter
    private lateinit var mAdapter: Adapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recycler)
        mPresenter = getPresenter()
        mAdapter = Adapter(this, mPresenter)
        mAdapter.setLayout(R.layout.activity_recycler_recycler)

        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@RecyclerActivity, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }

        mPresenter.loadInit()
    }

    inner class Adapter(owner: LifecycleOwner, presenter: Presenter) : RecyclerAdapter<String>(owner, presenter) {

        override fun bindContentViewHolder(binding: ViewDataBinding, content: String, position: Int) {
            (binding as? ActivityRecyclerRecyclerBinding)?.name = content
        }
    }

    class Presenter(app: Application) : RecyclerPresenter<String>(app) {

        override fun loadData(page: Int): List<String>? {
            Thread.sleep(1000)
            return (0..9).map { it.toString() }
        }
    }

}

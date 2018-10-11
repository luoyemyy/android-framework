package com.github.luoyemyy.framework.test.recycler

import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.luoyemyy.framework.ext.getPresenter
import com.github.luoyemyy.framework.mvp.recycler.SingleRecyclerAdapter
import com.github.luoyemyy.framework.mvp.recycler.RecyclerPresenter
import com.github.luoyemyy.framework.mvp.recycler.wrap
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityRecyclerBinding
import com.github.luoyemyy.framework.test.databinding.ActivityRecyclerRecyclerBinding

class RecyclerActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRecyclerBinding
    private lateinit var mPresenter: Presenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recycler)
        mPresenter = getPresenter()

        mBinding.recyclerView.wrap(Adapter(this, mPresenter))
        mBinding.swipeRefreshLayout.wrap(mPresenter)

        mPresenter.loadInit()
    }

    inner class Adapter(owner: LifecycleOwner, presenter: Presenter) : SingleRecyclerAdapter<String, ActivityRecyclerRecyclerBinding>(owner, presenter) {

        init {
            setLayout(R.layout.activity_recycler_recycler)
        }

        override fun bindContentViewHolder(binding: ActivityRecyclerRecyclerBinding, content: String, position: Int) {
            binding.name = content
            binding.executePendingBindings()
        }

        override fun setRefreshState(refreshing: Boolean) {
            mBinding.swipeRefreshLayout.isRefreshing = refreshing
        }
    }

    class Presenter(app: Application) : RecyclerPresenter<String>(app) {

        override fun loadData(page: Int): List<String>? {
            Thread.sleep(1000)
            return if (page < 2) {
                (0..2).map { ((page - 1) * 10 + it).toString() }
            } else {
                null
            }
        }

        fun e(){
            getDataSet().opData {

            }
        }
    }

}

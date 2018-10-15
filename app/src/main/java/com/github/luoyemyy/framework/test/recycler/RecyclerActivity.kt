package com.github.luoyemyy.framework.test.recycler

import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.luoyemyy.framework.bus.BusManager
import com.github.luoyemyy.framework.bus.BusMsg
import com.github.luoyemyy.framework.bus.BusResult
import com.github.luoyemyy.framework.ext.getPresenter
import com.github.luoyemyy.framework.ext.toast
import com.github.luoyemyy.framework.mvp.recycler.AbstractRecyclerPresenter
import com.github.luoyemyy.framework.mvp.recycler.AbstractSingleRecyclerAdapter
import com.github.luoyemyy.framework.mvp.recycler.wrap
import com.github.luoyemyy.framework.test.MainActivity
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityRecyclerBinding
import com.github.luoyemyy.framework.test.databinding.ActivityRecyclerRecyclerBinding

class RecyclerActivity : AppCompatActivity(), BusResult {

    private lateinit var mBinding: ActivityRecyclerBinding
    private lateinit var mPresenter: Presenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recycler)
        mPresenter = getPresenter()

        mBinding.recyclerView.wrap(Adapter(this, mPresenter))
        mBinding.swipeRefreshLayout.wrap(mPresenter)

        BusManager.setCallback(lifecycle, this, BUS_EVENT)

        mPresenter.loadInit()
    }

    override fun busResult(event: String, msg: BusMsg) {
        toast(message = event)
    }

    companion object {
        const val BUS_EVENT = "com.github.luoyemyy.framework.test.recycler.RecyclerActivity"
    }

    inner class Adapter(owner: LifecycleOwner, presenter: Presenter) : AbstractSingleRecyclerAdapter<String, ActivityRecyclerRecyclerBinding>(owner, presenter) {

        override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ActivityRecyclerRecyclerBinding {
            return ActivityRecyclerRecyclerBinding.inflate(inflater, parent, false)
        }

        override fun bindContentViewHolder(binding: ActivityRecyclerRecyclerBinding, content: String, position: Int) {
            binding.name = content
            binding.executePendingBindings()
        }

        override fun setRefreshState(refreshing: Boolean) {
            mBinding.swipeRefreshLayout.isRefreshing = refreshing
        }
    }

    class Presenter(app: Application) : AbstractRecyclerPresenter<String>(app) {

        override fun beforeLoadRefresh() {
            BusManager.post(BUS_EVENT)
            BusManager.post(MainActivity.BUS_EVENT)
        }

        override fun loadData(page: Int): List<String>? {
            Thread.sleep(1000)
            return if (page < 3) {
                (0..9).map { ((page - 1) * 10 + it).toString() }
            } else {
                null
            }
        }
    }

}

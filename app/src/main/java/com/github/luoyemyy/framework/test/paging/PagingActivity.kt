package com.github.luoyemyy.framework.test.paging

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.github.luoyemyy.framework.ext.getPresenter
import com.github.luoyemyy.framework.paging.PagingDecoration
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityPagingBinding

class PagingActivity : AppCompatActivity() {

    private lateinit var mPresenter: PagingPresenter
    private lateinit var mBinding: ActivityPagingBinding
    private lateinit var mAdapter: PagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_paging)

        mAdapter = PagingAdapter(this)
        mPresenter = getPresenter()
        mPresenter.also { presenter ->
            presenter.bundle = intent.extras
            presenter.liveData.observe(this, Observer {
                mAdapter.submitList(it)
                mBinding.recyclerView.invalidateItemDecorations()
            })
//            presenter.refreshLiveData.observe(this, Observer { mBinding.swipeRefreshLayout.isRefreshing = it ?: false })
//            presenter.emptyLiveData.observe(this, Observer { mBinding.layoutEmpty.visibility = if (it == true) View.GONE else View.VISIBLE })
        }
        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@PagingActivity, LinearLayoutManager.VERTICAL, false)
//            addItemDecoration(PagingDecoration(mBinding.layoutMore, mBinding.layoutEmpty))
            adapter = mAdapter
        }

        mBinding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                mPresenter.refreshData()
            }
        }
    }
}
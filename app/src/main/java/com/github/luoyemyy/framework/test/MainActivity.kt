package com.github.luoyemyy.framework.test

import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.github.luoyemyy.framework.ext.getPresenter
import com.github.luoyemyy.framework.mvp.recycler.RecyclerAdapter
import com.github.luoyemyy.framework.mvp.recycler.RecyclerPresenter
import com.github.luoyemyy.framework.test.databinding.ActivityMainBinding
import com.github.luoyemyy.framework.test.databinding.ActivityMainRecyclerBinding
import com.github.luoyemyy.framework.test.drawer.DrawerActivity
import com.github.luoyemyy.framework.test.mvp.MvpActivity
import com.github.luoyemyy.framework.test.navigation.NavigationActivity
import com.github.luoyemyy.framework.test.paging.PagingActivity
import com.github.luoyemyy.framework.test.recycler.RecyclerActivity
import com.github.luoyemyy.framework.test.status.StatusActivity


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mPresenter: Presenter
    private lateinit var mAdapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mPresenter = getPresenter()
        mAdapter = Adapter(this, mPresenter)

        mBinding.apply {
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = mAdapter
        }

        mPresenter.loadInit()
    }

    inner class Adapter(owner: LifecycleOwner, presenter: Presenter) : RecyclerAdapter<String>(owner, presenter) {

        init {
            setLayout(R.layout.activity_main_recycler)
            enableBindItemListener()
            enableLoadMore(false)
        }

        override fun bindContentViewHolder(binding: ViewDataBinding, content: String, position: Int) {
            (binding as ActivityMainRecyclerBinding).apply {
                name = content
                executePendingBindings()
            }
        }

        override fun onItemClickListener(view: View, position: Int) {
            when (position) {
                0 -> startActivity(Intent(this@MainActivity, StatusActivity::class.java))
                1 -> startActivity(Intent(this@MainActivity, DrawerActivity::class.java))
                2 -> startActivity(Intent(this@MainActivity, MvpActivity::class.java))
                3 -> startActivity(Intent(this@MainActivity, NavigationActivity::class.java))
                4 -> startActivity(Intent(this@MainActivity, PagingActivity::class.java))
                5 -> startActivity(Intent(this@MainActivity, RecyclerActivity::class.java))
            }
        }
    }

    class Presenter(app: Application) : RecyclerPresenter<String>(app) {

        override fun loadData(page: Int): List<String>? {
            return listOf(
                    "浸入状态栏",
                    "drawer",
                    "mvp",
                    "navigation",
                    "paging",
                    "recycler"
            )
        }
    }

}



package com.github.luoyemyy.framework.test

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.github.luoyemyy.framework.drawer.DrawerActivity
import com.github.luoyemyy.framework.mvp.MvpActivity
import com.github.luoyemyy.framework.mvp.recycler.AbstractAdapter
import com.github.luoyemyy.framework.mvp.recycler.AbstractRecyclerPresenter
import com.github.luoyemyy.framework.mvp.recycler.RecyclerData
import com.github.luoyemyy.framework.status.StatusActivity
import com.github.luoyemyy.framework.test.databinding.ActivityMainBinding
import com.github.luoyemyy.framework.test.databinding.ActivityMainRecyclerBinding
import com.github.luoyemyy.framework.utils.getPresenter

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mPresenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mPresenter = getPresenter(this)
        mPresenter.liveData.observe(this, Adapter())

        mBinding.apply {
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        }

        mPresenter.loadInit()
    }

    inner class Adapter : AbstractAdapter<String, ActivityMainRecyclerBinding>() {

        init {
            setLayout(R.layout.activity_main_recycler)
            bindItemClick()
        }

        override fun attachRecyclerView(adapter: AbstractAdapter<*, *>) {
            mBinding.recyclerView.adapter = adapter
        }

        override fun convert(helper: VH?, item: String?) {
            helper?.mBinding?.apply {
                name = item
                executePendingBindings()
            }
        }

        override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
            when (position) {
                0 -> startActivity(Intent(this@MainActivity, StatusActivity::class.java))
                1 -> startActivity(Intent(this@MainActivity, DrawerActivity::class.java))
                2 -> startActivity(Intent(this@MainActivity, MvpActivity::class.java))
            }
        }
    }

    class Presenter(app: Application) : AbstractRecyclerPresenter(app) {

        val liveData = MutableLiveData<RecyclerData<String>>()

        override fun loadInit(bundle: Bundle?) {
            liveData.value = RecyclerData.ofInit(listOf(
                    "浸入状态栏",
                    "drawer",
                    "mvp"
            ))
        }
    }

}



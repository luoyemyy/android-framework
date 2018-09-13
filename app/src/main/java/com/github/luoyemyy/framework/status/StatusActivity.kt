package com.github.luoyemyy.framework.status

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import com.github.luoyemyy.framework.mvp.recycler.AbstractAdapter
import com.github.luoyemyy.framework.mvp.recycler.AbstractRecyclerPresenter
import com.github.luoyemyy.framework.mvp.recycler.RecyclerData
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityMainRecyclerBinding
import com.github.luoyemyy.framework.test.databinding.ActivityStatusBinding
import com.github.luoyemyy.framework.utils.getPresenter


class StatusActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityStatusBinding
    private lateinit var mPresenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = Color.TRANSPARENT

        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_status)
        mPresenter = getPresenter(this)
        mPresenter.liveData.observe(this, Adapter())

        mBinding.apply {
            recyclerView.layoutManager = LinearLayoutManager(this@StatusActivity, LinearLayoutManager.VERTICAL, false)
        }

        mPresenter.loadInit()
    }

    inner class Adapter : AbstractAdapter<String, ActivityMainRecyclerBinding>() {

        init {
            setLayout(R.layout.activity_main_recycler)
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
    }

    class Presenter(app: Application) : AbstractRecyclerPresenter(app) {

        val liveData = MutableLiveData<RecyclerData<String>>()

        override fun loadInit(bundle: Bundle?) {
            liveData.value = RecyclerData.ofInit(listOf(
                    "浸入状态栏", "浸入状态栏", "浸入状态栏", "浸入状态栏", "浸入状态栏",
                    "浸入状态栏", "浸入状态栏", "浸入状态栏", "浸入状态栏", "浸入状态栏",
                    "浸入状态栏", "浸入状态栏", "浸入状态栏", "浸入状态栏", "浸入状态栏",
                    "浸入状态栏", "浸入状态栏", "浸入状态栏", "浸入状态栏", "浸入状态栏",
                    "浸入状态栏", "浸入状态栏", "浸入状态栏", "浸入状态栏", "浸入状态栏"
            ))
        }
    }
}
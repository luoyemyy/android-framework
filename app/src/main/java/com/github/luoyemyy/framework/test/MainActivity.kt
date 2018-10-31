package com.github.luoyemyy.framework.test

import android.Manifest
import android.app.Application
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.luoyemyy.framework.bus.BusManager
import com.github.luoyemyy.framework.bus.BusMsg
import com.github.luoyemyy.framework.bus.BusResult
import com.github.luoyemyy.framework.ext.getPresenter
import com.github.luoyemyy.framework.ext.toast
import com.github.luoyemyy.framework.mvp.recycler.*
import com.github.luoyemyy.framework.permission.PermissionManager
import com.github.luoyemyy.framework.test.databinding.ActivityMainBinding
import com.github.luoyemyy.framework.test.databinding.ActivityMainRecyclerBinding
import com.github.luoyemyy.framework.test.drawer.DrawerActivity
import com.github.luoyemyy.framework.test.exoplayer.ExoPlayerActivity
import com.github.luoyemyy.framework.test.mvp.MvpActivity
import com.github.luoyemyy.framework.test.navigation.NavigationActivity
import com.github.luoyemyy.framework.test.paging.PagingActivity
import com.github.luoyemyy.framework.test.recycler.RecyclerActivity
import com.github.luoyemyy.framework.test.status.StatusActivity
import com.github.luoyemyy.framework.test.transition.TransitionActivity


class MainActivity : AppCompatActivity(), BusResult {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mPresenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mPresenter = getPresenter()
        mPresenter.setup(this, Adapter())

        mBinding.recyclerView.setLinearManager()

        BusManager.setCallback(lifecycle, this, BUS_EVENT)

        mPresenter.loadInit()
    }

    override fun busResult(event: String, msg: BusMsg) {
        toast(message = event)
    }

    companion object {
        const val BUS_EVENT = "com.github.luoyemyy.framework.test.MainActivity"
    }

    inner class Adapter : AbstractSingleRecyclerAdapter<String, ActivityMainRecyclerBinding>(mBinding.recyclerView) {

        override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ActivityMainRecyclerBinding {
            return ActivityMainRecyclerBinding.inflate(inflater, parent, false)
        }

        override fun bindContentViewHolder(binding: ActivityMainRecyclerBinding, content: String, position: Int) {
            binding.apply {
                name = content
                executePendingBindings()
            }
        }

        override fun enableLoadMore(): Boolean {
            return false
        }

        override fun onItemClickListener(vh: VH<ActivityMainRecyclerBinding>, view: View?) {
            when (vh.adapterPosition) {
                0 -> startActivity(Intent(this@MainActivity, StatusActivity::class.java))
                1 -> startActivity(Intent(this@MainActivity, DrawerActivity::class.java))
                2 -> startActivity(Intent(this@MainActivity, MvpActivity::class.java))
                3 -> startActivity(Intent(this@MainActivity, NavigationActivity::class.java))
                4 -> startActivity(Intent(this@MainActivity, PagingActivity::class.java))
                5 -> startActivity(Intent(this@MainActivity, RecyclerActivity::class.java))
                6 -> {
                    PermissionManager.newFuture().withPass {
                        toast(message = "ok")
                    }.withDenied { _, _ ->
                        toast(message = "fail")
                    }.request(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET))
                }
                7 -> startActivity(Intent(this@MainActivity, TransitionActivity::class.java))
                8 -> startActivity(Intent(this@MainActivity, ExoPlayerActivity::class.java))
            }
        }

    }

    class Presenter(app: Application) : AbstractRecyclerPresenter<String>(app) {

        override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<String>? {
            return if (paging.current() == 1L) listOf(
                    "浸入状态栏",
                    "drawer",
                    "mvp",
                    "navigation",
                    "paging",
                    "recycler",
                    "permission",
                    "transition",
                    "exoPlayer"
            ) else listOf()
        }
    }

}



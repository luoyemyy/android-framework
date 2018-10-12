package com.github.luoyemyy.framework.test

import android.Manifest
import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.luoyemyy.framework.ext.getPresenter
import com.github.luoyemyy.framework.ext.toast
import com.github.luoyemyy.framework.mvp.recycler.AbstractRecyclerPresenter
import com.github.luoyemyy.framework.mvp.recycler.AbstractSingleRecyclerAdapter
import com.github.luoyemyy.framework.mvp.recycler.wrap
import com.github.luoyemyy.framework.permission.PermissionPresenter
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

        mBinding.recyclerView.wrap(Adapter(this, mPresenter))

        getPresenter<PermissionPresenter>()
                .create(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE))
                .withPass {
                    mPresenter.loadInit()
                }
                .withDenied { future, _ ->
                    future.toSettings(this, "需要设置一些权限")
                }
                .request(this)

    }

    inner class Adapter(owner: LifecycleOwner, presenter: Presenter) : AbstractSingleRecyclerAdapter<String, ActivityMainRecyclerBinding>(owner, presenter) {

        init {
            setLayout(R.layout.activity_main_recycler)
            enableItemClickListener()
            enableLoadMore(false)
        }

        override fun bindContentViewHolder(binding: ActivityMainRecyclerBinding, content: String, position: Int) {
            binding.apply {
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
                6 -> toast(supportFragmentManager.fragments.joinToString {
                    it::class.java.canonicalName ?: ""
                })
            }
        }
    }

    class Presenter(app: Application) : AbstractRecyclerPresenter<String>(app) {

        override fun loadData(page: Int): List<String>? {
            return listOf(
                    "浸入状态栏",
                    "drawer",
                    "mvp",
                    "navigation",
                    "paging",
                    "recycler",
                    "log"
            )
        }
    }

}



package com.github.luoyemyy.framework.test.navigation

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.luoyemyy.framework.mvp.AbstractPresenter
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.FragmentHostBinding
import com.github.luoyemyy.framework.test.navigation.tab.TabFragment

class HostFragment : Fragment() {

    private lateinit var mBinding: FragmentHostBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentHostBinding.inflate(inflater, container, false).apply { mBinding = this }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding.apply {

            toolbar.inflateMenu(R.menu.fragment_host)

            viewPager.adapter = Adapter(childFragmentManager)
            viewPager.offscreenPageLimit=3
            tabLayout.apply {
                setupWithViewPager(viewPager)
                getTabAt(0)!!.setIcon(R.drawable.ic_extension)
                getTabAt(1)!!.setIcon(R.drawable.ic_contact)
                getTabAt(2)!!.setIcon(R.drawable.ic_verified)
            }

        }
    }

    class Presenter(app: Application) : AbstractPresenter<String>(app) {

        override fun load(bundle: Bundle?) {

        }
    }

    class Adapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        override fun getItem(p0: Int): Fragment = TabFragment.getInstance("tab$p0")
        override fun getCount(): Int = 3
    }
}
package com.github.luoyemyy.framework.test.navigation.tab

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.luoyemyy.config.fragment.VisibleLoadFragment
import com.github.luoyemyy.framework.test.databinding.FragmentTab1Binding

class TabFragment : VisibleLoadFragment() {

    private lateinit var mBinding: FragmentTab1Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentTab1Binding.inflate(inflater, container, false).apply { mBinding = this }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding.text = arguments?.getString("tab")
    }

    companion object {
        fun getInstance(tab: String) = TabFragment().apply {
            arguments = Bundle().apply { putString("tab", tab) }
        }
    }

    override fun loadData(bundle: Bundle?) {
        Log.e("TabFragment", "loadData:  ${bundle?.getString("tab")}")
    }
}
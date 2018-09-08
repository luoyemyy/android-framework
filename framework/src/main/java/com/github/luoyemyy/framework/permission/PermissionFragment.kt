package com.github.luoyemyy.framework.permission

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

internal class PermissionFragment : Fragment() {

    private var permissionPresenter: PermissionPresenter? = null

    companion object {

        private val TAG = "${PermissionFragment::class.java.name}_requestPermission"

        fun getInstance(activity: FragmentActivity): PermissionFragment {
            val manager = activity.supportFragmentManager
            val fragment = manager.findFragmentByTag(TAG)
            return if (fragment != null) {
                fragment as PermissionFragment
            } else {
                val permissionFragment = PermissionFragment()
                manager.beginTransaction().add(permissionFragment, TAG).commit()
                permissionFragment
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        permissionPresenter = ViewModelProviders.of(activity!!).get(PermissionPresenter::class.java)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionPresenter?.result(requestCode, permissions, grantResults)
    }
}
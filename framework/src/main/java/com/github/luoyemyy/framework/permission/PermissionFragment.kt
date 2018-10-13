package com.github.luoyemyy.framework.permission

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.github.luoyemyy.framework.ext.getPresenter

internal class PermissionFragment : Fragment() {

    private var mPermissionFuture: PermissionFuture? = null

    companion object {

        const val REQUEST_CODE = "requestCode"
        const val REQUEST_Permission = "requestPermission"

        fun startPermissionFragment(requestCode: Int, requestPermission: Array<String>, activity: FragmentActivity) {
            val permissionFragment = PermissionFragment().apply {
                arguments = Bundle().apply {
                    putInt(REQUEST_CODE, requestCode)
                    putStringArray(REQUEST_Permission, requestPermission)

                }
            }
            activity.supportFragmentManager.beginTransaction().add(permissionFragment, null).commit()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val requestCode = arguments?.getInt(REQUEST_CODE) ?: -1
        val requestPermission = arguments?.getStringArray(REQUEST_Permission)
        if (requestPermission != null) {
            requireActivity().getPresenter<PermissionPresenter>().getFuture(requestCode)?.apply {
                mPermissionFuture = this
                requestPermissions(requestPermission, requestCode)
                return
            }
        }
        closeRequest()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        mPermissionFuture?.result(requestCode, permissions, grantResults)
        closeRequest()
    }

    private fun closeRequest() {
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }
}
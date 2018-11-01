package com.github.luoyemyy.framework.permission

import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

internal class PermissionFragment : Fragment() {

    companion object {

        const val REQUEST_PERMISSION = "requestPermission"

        fun startPermissionFragment(fragmentManager: FragmentManager, requestPermission: Array<String>) {
            val permissionFragment = PermissionFragment().apply {
                arguments = Bundle().apply {
                    putStringArray(REQUEST_PERMISSION, requestPermission)
                }
            }
            fragmentManager.beginTransaction().add(permissionFragment, null).commit()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val permissions = arguments?.getStringArray(REQUEST_PERMISSION)
        if (permissions != null) {
            requestPermissions(permissions, PermissionManager.REQUEST_CODE)
        } else {
            closeRequest()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PermissionManager.REQUEST_CODE) {
            val deniedList = mutableListOf<String>()
            grantResults.forEachIndexed { index, i ->
                if (i == PackageManager.PERMISSION_DENIED) {
                    deniedList.add(permissions[index])
                }
            }
            val parent = parentFragment
            if (parent == null) {
                ViewModelProviders.of(requireActivity()).get(PermissionPresenter::class.java).postValue(deniedList.toTypedArray())
            } else {
                ViewModelProviders.of(parent).get(PermissionPresenter::class.java).postValue(deniedList.toTypedArray())
            }
        }
        closeRequest()
    }

    private fun closeRequest() {
        val parent = parentFragment
        if (parent == null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        } else {
            parent.childFragmentManager.beginTransaction().remove(this).commit()
        }
    }
}
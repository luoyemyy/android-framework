package com.github.luoyemyy.framework.permission

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat

class PermissionFuture(private val mRequestCode: Int, private val mPermissions: Array<String>) {

    private var mPassRunnable: (() -> Unit)? = null
    private var mDeniedRunnable: ((msg: String, permissions: Array<String>) -> Unit)? = null
    private lateinit var mRequestPermission: Array<String>
    private var mRequest = false

    fun withPass(pass: (() -> Unit)): PermissionFuture {
        mPassRunnable = pass
        return this
    }

    fun withDenied(denied: ((msg: String, permissions: Array<String>) -> Unit)): PermissionFuture {
        mDeniedRunnable = denied
        return this
    }

    fun request(activity: FragmentActivity) {
        if (mPermissions.isEmpty()) {
            mPassRunnable?.invoke()
            return
        }
        mRequestPermission = mPermissions.filter { ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_DENIED }.toTypedArray()
        if (mRequestPermission.isEmpty()) {
            mPassRunnable?.invoke()
            return
        }
        val permissionFragment = PermissionFragment.getInstance(activity)
        Handler().post {
            permissionFragment.requestPermissions(mRequestPermission, mRequestCode)
            mRequest = true
        }
    }

    fun result(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (mRequest && requestCode == mRequestCode && grantResults.size == mRequestPermission.size) {
            val deniedList = mutableListOf<String>()
            grantResults.forEachIndexed { index, i ->
                if (i == PackageManager.PERMISSION_DENIED) {
                    deniedList.add(permissions[index])
                }
            }
            if (deniedList.isEmpty()) {
                mPassRunnable?.invoke()
            } else {
                mDeniedRunnable?.invoke("未获得权限列表", deniedList.toTypedArray())
            }
            mPassRunnable = null
            mDeniedRunnable = null
        }
    }

    companion object {
        @JvmStatic
        fun of(requestCode: Int, permissions: Array<String>) = PermissionFuture(requestCode, permissions)

        fun toSettings(activity: Activity) {
            activity.startActivity(Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.parse("package:${activity.packageName}")
            })
        }
    }

}

package com.github.luoyemyy.framework.permission

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat

class PermissionFuture(private val mRequestCode: Int, private val mPermissions: Array<String>) {

    private var mPassRunnable: (() -> Unit)? = null
    private var mDeniedRunnable: ((future: PermissionFuture, permissions: Array<String>) -> Unit)? = null
    private lateinit var mRequestPermission: Array<String>

    fun withPass(pass: (() -> Unit)): PermissionFuture {
        mPassRunnable = pass
        return this
    }

    /**
     * 可以使用 future.toSettings(activity,msg)，跳到应用详情页去授权
     */
    fun withDenied(denied: ((future: PermissionFuture, permissions: Array<String>) -> Unit)): PermissionFuture {
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
        PermissionFragment.startPermissionFragment(mRequestCode, mRequestPermission, activity)
    }

    internal fun result(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == mRequestCode && grantResults.size == mRequestPermission.size) {
            val deniedList = mutableListOf<String>()
            grantResults.forEachIndexed { index, i ->
                if (i == PackageManager.PERMISSION_DENIED) {
                    deniedList.add(permissions[index])
                }
            }
            if (deniedList.isEmpty()) {
                mPassRunnable?.invoke()
            } else {
                mDeniedRunnable?.invoke(this, deniedList.toTypedArray())
            }
            mPassRunnable = null
            mDeniedRunnable = null
        }
    }

    fun toSettings(activity: Activity, msg: String, cancel: String = "取消", sure: String = "去设置") {
        AlertDialog.Builder(activity).setMessage(msg).setNegativeButton(cancel, null).setPositiveButton(sure) { _, _ ->
            val intent = Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.parse("package:${activity.packageName}")
            }
            if (intent.resolveActivity(activity.packageManager) != null) {
                activity.startActivity(intent)
            }
        }.show()
    }

    companion object {
        @JvmStatic
        fun of(requestCode: Int, permissions: Array<String>) = PermissionFuture(requestCode, permissions)
    }

}

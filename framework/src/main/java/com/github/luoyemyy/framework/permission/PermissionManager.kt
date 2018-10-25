package com.github.luoyemyy.framework.permission

import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat

object PermissionManager {

    const val REQUEST_CODE = 95

    fun newFuture(): Future = Future()

    fun withPass(pass: (() -> Unit)): Future {
        return Future().withPass(pass)
    }

    class Future internal constructor() : Observer<Array<String>> {

        private var mPresenter: PermissionPresenter? = null
        private var mPassRunnable: (() -> Unit)? = null
        private var mDeniedRunnable: ((future: Future, permissions: Array<String>) -> Unit)? = null

        override fun onChanged(deniedArray: Array<String>?) {
            if (deniedArray == null) {
                return
            }
            if (deniedArray.isEmpty()) {
                mPassRunnable?.invoke()
            } else {
                mDeniedRunnable?.invoke(this, deniedArray)
            }

            //clear
            mPresenter?.removeObserver(this)
            mPresenter = null
            mPassRunnable = null
            mDeniedRunnable = null
        }

        fun withPass(pass: (() -> Unit)): Future {
            mPassRunnable = pass
            return this
        }

        /**
         * 可以使用 future.toSettings(activity,msg)，跳到应用详情页去授权
         */
        fun withDenied(denied: ((future: Future, permissions: Array<String>) -> Unit)): Future {
            mDeniedRunnable = denied
            return this
        }

        fun request(activity: FragmentActivity, permissions: Array<String>) {

            if (permissions.isEmpty()) {
                mPassRunnable?.invoke()
                return
            }
            val requestPermission = permissions.filter { ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_DENIED }.toTypedArray()
            if (requestPermission.isEmpty()) {
                mPassRunnable?.invoke()
                return
            }

            mPresenter = ViewModelProviders.of(activity).get(PermissionPresenter::class.java).also {
                it.addObserver(activity, this)
            }

            PermissionFragment.startPermissionFragment(activity, requestPermission)
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

    }


}
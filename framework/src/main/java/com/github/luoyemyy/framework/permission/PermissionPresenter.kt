package com.github.luoyemyy.framework.permission

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.SparseArray

/**
 *  example:
 *
 *  getPresenter<PermissionPresenter>(this) // this is FragmentActivity
 *      .create(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE))
 *      .withPass {
 *          alert("success")
 *      }
 *      .withDenied { _, _ ->
 *          alert("failure")
 *      }
 *      .request(this)
 *
 */
class PermissionPresenter(app: Application) : AndroidViewModel(app) {

    private val permissionFutureArray: SparseArray<PermissionFuture> = SparseArray()

    fun create(permissions: Array<String>): PermissionFuture {
        val code = generatorRequestCode()
        val future = PermissionFuture.of(code, permissions)
        permissionFutureArray.put(code, future)
        return future
    }

    private fun generatorRequestCode(): Int {
        (1..999).forEach {
            if (permissionFutureArray.indexOfKey(it) == -1) {
                return it
            }
        }
        //不可能1-999都有的吧
        return 1
    }


    /**
     * PermissionFragment 回调此方法
     */
    fun result(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val future = permissionFutureArray.get(requestCode) ?: return
        future.result(requestCode, permissions, grantResults)
        permissionFutureArray.delete(requestCode)
    }
}
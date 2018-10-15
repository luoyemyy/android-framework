package com.github.luoyemyy.framework.permission

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.SparseArray

/**
 *  example:
 *
 *  ViewModelProviders.of(fragmentActivity).get(PermissionPresenter::class.java)
 *      .create(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE))
 *      .withPass {
 *          alert("success")
 *      }
 *      .withDenied { future, permissions ->
 *          val msg = getDesc(permissions) // 获取未获得权限的描述
 *          future.toSettings(fragmentActivity,msg)
 *      }
 *      .request(fragmentActivity)
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
     * 取出后，立即从缓存中删除
     */
    internal fun getFuture(requestCode: Int): PermissionFuture? {
        val future = permissionFutureArray.get(requestCode)
        permissionFutureArray.delete(requestCode)
        return future
    }
}
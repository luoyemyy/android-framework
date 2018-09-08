package com.github.luoyemyy.framework.async

import java.util.*

open class ModelBack<T> {

    /**
     * 状态信息
     */
    var isSuccess: Boolean = false
    var msg: String = "ok"

    /**
     * 数据信息
     */
    var data: T? = null
    var listData: List<T>? = null
    var int: Int = 0
    var long: Long = 0
    var boolean: Boolean = false
    var string: String? = null
    var any: Any? = null
    val mMap: MutableMap<String, Any> by lazy { HashMap<String, Any>() }

    fun setSuccess(msg: String = "ok"): ModelBack<T> {
        this.isSuccess = true
        this.msg = msg
        return this
    }

    fun setFailure(msg: String = "fail"): ModelBack<T> {
        this.isSuccess = false
        this.msg = msg
        return this
    }

    companion object {
        @JvmStatic
        fun <T> get(): ModelBack<T> = ModelBack()

        @JvmStatic
        fun <T> error(): ModelBack<T> = ModelBack<T>().setFailure()

        @JvmStatic
        fun <T> success(): ModelBack<T> = ModelBack<T>().setSuccess()
    }
}

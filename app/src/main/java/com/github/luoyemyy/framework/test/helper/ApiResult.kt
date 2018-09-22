package com.github.luoyemyy.framework.test.helper

import com.github.luoyemyy.framework.async.AsyncRun

class ApiResult<T> : AsyncRun.Result {
    override var isSuccess: Boolean = false

    var data: T? = null
    var list: List<T>? = null
}
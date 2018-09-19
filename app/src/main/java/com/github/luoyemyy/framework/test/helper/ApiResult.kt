package com.github.luoyemyy.framework.test.helper

import com.github.luoyemyy.framework.async.AsyncRunResult

class ApiResult<T> : AsyncRunResult {
    override var isSuccess: Boolean = false

    var data: T? = null
    var list: List<T>? = null
}
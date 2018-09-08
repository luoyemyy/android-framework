package com.github.luoyemyy.framework.async

class SimpleBack(isSuccess: Boolean = false, msg: String = "fail") : ModelBack<Any>() {
    init {
        this.isSuccess = isSuccess
        this.msg = msg
    }
}
package com.github.luoyemyy.framework.test.helper

import com.github.luoyemyy.framework.api.AbstractApiManager

class ApiManager : AbstractApiManager() {
    override fun baseDevUrl(): String {
        return ""
    }

    override fun baseTestUrl(): String {
        return ""
    }

    override fun baseProUrl(): String {
        return ""
    }

}
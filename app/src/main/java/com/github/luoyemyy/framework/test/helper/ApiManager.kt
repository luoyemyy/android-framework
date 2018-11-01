package com.github.luoyemyy.framework.test.helper

import com.github.luoyemyy.config.api.AbstractApiManager

class ApiManager : AbstractApiManager() {
    override fun baseDemoUrl(): String {
        return ""
    }

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
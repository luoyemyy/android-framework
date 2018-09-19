package com.github.luoyemyy.framework.app

class Profile(private var type: Int) {

    fun isDev() = type == DEV

    fun isTest() = type == TEST

    fun isPro() = type == PRO

    fun setNewType(type: Int) {
        this.type = type
    }

    companion object {
        const val DEV = 1
        const val TEST = 2
        const val PRO = 3
    }
}
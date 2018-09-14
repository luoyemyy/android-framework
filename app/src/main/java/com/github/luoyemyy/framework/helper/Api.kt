package com.github.luoyemyy.framework.helper

import retrofit2.Call

interface Api {
    fun login(): Call<String>
}
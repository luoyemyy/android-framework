package com.github.luoyemyy.framework.test.helper

import com.github.luoyemyy.framework.async.AsyncRun
import retrofit2.Call

fun getApi(): Api = ApiManager().getApi()

fun <T, R> Call<ApiResult<T>>.create(map: (ApiResult<T>) -> ApiResult<R>): AsyncRun.Call<ApiResult<R>> {
    val error = ApiResult<R>()
    return AsyncRun.single().Call(error).create { _ ->
        val result = this.execute()
        if (result.isSuccessful) {
            result.body()?.let {
                map(it)
            } ?: error
        } else {
            error
        }
    }
}

fun <T> Call<ApiResult<T>>.create(): AsyncRun.Call<ApiResult<T>> {
    val error = ApiResult<T>()
    return AsyncRun.single().Call(error).create {
        val result = this.execute()
        if (result.isSuccessful) {
            result.body() ?: error
        } else {
            error
        }
    }
}
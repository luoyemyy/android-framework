package com.github.luoyemyy.framework.test.helper

import com.github.luoyemyy.framework.async.AsyncRun
import retrofit2.Call

fun getApi(): Api = ApiManager().getApi()

fun <T, R> Call<ApiResult<T>>.create(map: (T) -> R): AsyncRun.Call<ApiResult<R>> {
    return AsyncRun.Call<ApiResult<R>>().create { _ ->
        val result = this.execute()
        if (result.isSuccessful) {
            result.body()?.map(map) ?: ApiResult()
        } else {
            ApiResult()
        }
    }
}

fun <T> Call<ApiResult<T>>.create(): AsyncRun.Call<ApiResult<T>> {
    val error = ApiResult<T>()
    return AsyncRun.Call<ApiResult<T>>().create {
        val result = this.execute()
        if (result.isSuccessful) {
            result.body() ?: error
        } else {
            error
        }
    }
}
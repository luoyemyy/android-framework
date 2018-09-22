package com.github.luoyemyy.framework.test.helper

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.github.luoyemyy.framework.async.AsyncRun
import retrofit2.Call

inline fun <reified T : AndroidViewModel> getPresenter(fragment: Fragment): T = ViewModelProviders.of(fragment).get(T::class.java)

inline fun <reified T : AndroidViewModel> getPresenter(activity: AppCompatActivity): T = ViewModelProviders.of(activity).get(T::class.java)

fun getApi(): Api = ApiManager().getApi()

fun <T> Call<ApiResult<T>>.success(ok: (ApiResult<T>) -> Unit): AsyncRun.Call<ApiResult<T>> {
    val error = ApiResult<T>()
    return AsyncRun.single().Call(error).create {
        val result = this.execute()
        if (result.isSuccessful) {
            result.body() ?: error
        } else {
            error
        }
    }.success(ok)
}
package com.github.luoyemyy.framework.helper

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.github.luoyemyy.framework.async.AsyncRun
import com.github.luoyemyy.framework.async.ModelBack
import com.github.luoyemyy.framework.async.SimpleBack

inline fun <reified T : AndroidViewModel> getPresenter(fragment: Fragment): T = ViewModelProviders.of(fragment).get(T::class.java)

inline fun <reified T : AndroidViewModel> getPresenter(activity: AppCompatActivity): T = ViewModelProviders.of(activity).get(T::class.java)

fun getApi(): Api = ApiManager().getApi()

fun newSimpleCall(): AsyncRun.Call<SimpleBack> = AsyncRun.single().newSimpleCall()

fun <T> newCall(): AsyncRun.Call<ModelBack<T>> = AsyncRun.single().newCall()
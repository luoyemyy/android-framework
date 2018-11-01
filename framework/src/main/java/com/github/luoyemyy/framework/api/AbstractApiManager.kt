package com.github.luoyemyy.framework.api

import com.github.luoyemyy.framework.app.AppInfo
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class AbstractApiManager {

    fun baseUrl(): String {
        return when {
            AppInfo.profile.isDev() -> baseDevUrl()
            AppInfo.profile.isTest() -> baseTestUrl()
            AppInfo.profile.isDemo() -> baseDemoUrl()
            AppInfo.profile.isPro() -> baseProUrl()
            else -> baseDevUrl()
        }
    }

    abstract fun baseDevUrl(): String
    abstract fun baseTestUrl(): String
    abstract fun baseDemoUrl(): String
    abstract fun baseProUrl(): String

    open fun client(): OkHttpClient.Builder = OkHttpClient.Builder().addInterceptor(LogInterceptor())

    open fun converter(): Converter.Factory? = GsonConverterFactory.create()

    inline fun <reified T> getApi(): T {
        return Retrofit.Builder().baseUrl(baseUrl()).client(client().build()).apply {
            converter()?.also {
                addConverterFactory(it)
            }
        }.build().create(T::class.java)
    }
}

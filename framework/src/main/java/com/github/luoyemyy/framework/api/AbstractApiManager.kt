package com.github.luoyemyy.framework.api

import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class AbstractApiManager {

    abstract fun baseUrl(): String

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



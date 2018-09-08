package com.github.luoyemyy.framework.utils

import android.util.Log

fun loge(tag: String, message: String?, e: Throwable? = null) = Log.e(tag, message, e)
fun logd(tag: String, message: String?, e: Throwable? = null) = Log.d(tag, message, e)
fun logi(tag: String, message: String?, e: Throwable? = null) = Log.i(tag, message, e)
fun logw(tag: String, message: String?, e: Throwable? = null) = Log.w(tag, message, e)
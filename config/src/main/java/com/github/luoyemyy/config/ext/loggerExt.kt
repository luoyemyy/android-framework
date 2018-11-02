package com.github.luoyemyy.config.ext

import com.github.luoyemyy.logger.Logger

fun loge(tag: String, message: String, e: Throwable? = null) = Logger.e(tag, message, e)

fun logd(tag: String, message: String, e: Throwable? = null) = Logger.d(tag, message, e)

fun logi(tag: String, message: String, e: Throwable? = null) = Logger.i(tag, message, e)

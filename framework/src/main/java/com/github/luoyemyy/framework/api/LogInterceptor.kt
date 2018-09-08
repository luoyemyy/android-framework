package com.github.luoyemyy.framework.api

import android.util.Log
import com.github.luoyemyy.framework.app.Logger
import okhttp3.*
import okio.Buffer
import java.lang.StringBuilder

/**
 * 日志拦截器
 */
class LogInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return afterLog(chain.proceed(preLog(chain.request())))
    }

    private fun afterLog(response: Response): Response {
        val size = response.body()?.contentLength() ?: 0
        if (size > 0L) {
            Logger.i("LogInterceptor", "<<<<<<:${response.peekBody(size).string()}")
        }
        return response
    }

    private fun preLog(request: Request): Request {
        val method = request.method().toUpperCase()
        val logBuilder = StringBuilder()
        when (method) {
            "GET" -> logBuilder.append(">>>>>>:get,${request.url()}")
            "POST" -> logBuilder.append(">>>>>>:post,${request.url()},${postBodyParam(request.body())}")
        }
        Log.e("LogInterceptor", "preLog:  $logBuilder")
        return request
    }

    private fun postBodyParam(body: RequestBody?): String {
        return when (body) {
            null -> ""
            is FormBody -> logFormBody(body)
            is MultipartBody -> logMultipartBody(body)
            else -> logOtherBody(body)
        }
    }

    private fun logOtherBody(body: RequestBody) = Buffer().run { body.writeTo(this);readString(charset("utf-8")) }

    private fun logFormBody(body: FormBody) = logOtherBody(body)//(0 until body.size()).joinToString("&") { "${body.name(it)}=${body.value(it)}" }

    private fun logMultipartBody(body: MultipartBody): String {
        return body.parts().joinToString("&") {
            val disposition = it.headers()?.get("Content-Disposition")
            var name: String? = null
            var value: String? = null
            if (disposition != null) {
                //Content-Disposition: form-data;name="pic"; filename="photo.jpg"
                val arrays = disposition.split('"')
                if (arrays.size >= 2) {
                    name = arrays[1]
                    value = if (arrays.size >= 4 && disposition.contains("filename")) {
                        arrays[3]
                    } else {
                        logOtherBody(it.body())
                    }
                }
            }
            if (name != null && value != null) {
                "$name=$value"
            } else {
                ""
            }
        }
    }
}
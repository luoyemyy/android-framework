@file:Suppress("unused")

package com.github.luoyemyy.framework.ext

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.widget.Toast

/**
 * context
 */
fun Context.dp2px(dp: Int) = Math.round(resources.displayMetrics.density * dp)

fun Context.hasPermission(vararg permissions: String): Boolean = if (permissions.isEmpty()) false else permissions.none { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED }

fun Context.toast(messageId: Int = 0, message: String = "toast message") = Toast.makeText(this, if (messageId > 0) getString(messageId) else message, Toast.LENGTH_SHORT).show()

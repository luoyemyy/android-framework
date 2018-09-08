package com.github.luoyemyy.framework.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast

fun Context.dp2px(dp: Int) = Math.round(resources.displayMetrics.density * dp)

fun Context.hasPermission(vararg permissions: String): Boolean = if (permissions.isEmpty()) false else permissions.none { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED }

fun Activity.alert(string: String) = AlertDialog.Builder(this).setMessage(string).setPositiveButton("ok", null).create().show()

fun Activity.alert(@StringRes stringRes: Int) = AlertDialog.Builder(this).setMessage(stringRes).setPositiveButton("ok", null).create().show()

fun Context.toast(string: String) = Toast.makeText(this, string, Toast.LENGTH_SHORT).show()

fun Context.toast(@StringRes stringRes: Int) = Toast.makeText(this, stringRes, Toast.LENGTH_SHORT).show()

package com.github.luoyemyy.framework.utils

import android.content.Context
import android.content.SharedPreferences

import com.github.luoyemyy.framework.app.AppInfo

fun Context.spfBool(key: String): Boolean {
    return this.getSharedPreferences(AppInfo.preferencesName, 0).getBoolean(key, false)
}

fun Context.spfBool(key: String, value: Boolean) {
    this.getSharedPreferences(AppInfo.preferencesName, 0).edit().putBoolean(key, value).apply()
}

fun Context.spfInt(key: String): Int {
    return this.getSharedPreferences(AppInfo.preferencesName, 0).getInt(key, 0)
}

fun Context.spfInt(key: String, value: Int) {
    this.getSharedPreferences(AppInfo.preferencesName, 0).edit().putInt(key, value).apply()
}

fun Context.spfLong(key: String): Long {
    return this.getSharedPreferences(AppInfo.preferencesName, 0).getLong(key, 0)
}

fun Context.spfLong(key: String, value: Long) {
    this.getSharedPreferences(AppInfo.preferencesName, 0).edit().putLong(key, value).apply()
}

fun Context.spfString(key: String): String? {
    return this.getSharedPreferences(AppInfo.preferencesName, 0).getString(key, null)
}

fun Context.spfString(key: String, value: String) {
    this.getSharedPreferences(AppInfo.preferencesName, 0).edit().putString(key, value).apply()
}

fun Context.spfClear() {
    this.getSharedPreferences(AppInfo.preferencesName, 0).edit().clear().apply()
}

fun Context.editor(): SharedPreferences.Editor {
    return this.getSharedPreferences(AppInfo.preferencesName, 0).edit()
}

fun Context.spf(): SharedPreferences {
    return this.getSharedPreferences(AppInfo.preferencesName, 0)
}

@file:Suppress("unused")

package com.github.luoyemyy.framework.ext

import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.github.luoyemyy.framework.app.AppInfo
import com.github.luoyemyy.framework.app.Logger
import com.github.luoyemyy.framework.async.AsyncRun
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*

/**
 * thread run
 */
fun runOnWorkerThread(run: () -> Unit) = AsyncRun.newCall<Any>().create { run() }

fun runOnMainThread(run: () -> Unit) = Handler(Looper.getMainLooper()).post(run)

/**
 * context
 */
fun Context.dp2px(dp: Int) = Math.round(resources.displayMetrics.density * dp)

fun Context.hasPermission(vararg permissions: String): Boolean = if (permissions.isEmpty()) false else permissions.none { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED }

fun Context.toast(stringId: Int = 0, string: String = "toast message") = Toast.makeText(this, if (stringId > 0) getString(stringId) else string, Toast.LENGTH_SHORT).show()

fun Activity.alert(messageId: Int = 0, message: String = "alert message", okButtonId: Int = 0, okButton: String = "ok") = AlertDialog.Builder(this).setMessage(if (messageId > 0) getString(messageId) else message).setPositiveButton(if (okButtonId > 0) getString(okButtonId) else okButton, null).create().show()

fun Activity.confirm(messageId: Int = 0, message: String = "confirm message", cancelButtonId: Int = 0, cancelButton: String = "cancel", cancelCallback: () -> Unit = {}, okButtonId: Int = 0, okButton: String = "ok", okCallback: () -> Unit = {}) = AlertDialog.Builder(this).setMessage(if (messageId > 0) getString(messageId) else message).setNegativeButton(if (cancelButtonId > 0) getString(cancelButtonId) else cancelButton) { _, _ -> cancelCallback() }.setPositiveButton(if (okButtonId > 0) getString(okButtonId) else okButton) { _, _ -> okCallback() }.create().show()

/**
 * 浸入式状态栏
 * 使用drawerLayout时可以在NavigationView中增加app:insetForeground="@android:color/transparent"，侧滑栏适配浸入式
 */
fun Activity.immerse() {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN and View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = Color.TRANSPARENT
}

/**
 * 状态栏黑色字体
 */
fun Activity.lightStatusBar() {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

/**
 *  empty
 */
fun String?.empty(): Boolean = this == null || isEmpty()

fun String?.notEmpty(): Boolean = this != null && isNotEmpty()

fun Collection<*>?.empty(): Boolean = this == null || isEmpty()

fun Collection<*>?.notEmpty(): Boolean = this != null && isNotEmpty()

fun Map<*, *>?.empty(): Boolean = this == null || isEmpty()

fun Map<*, *>?.notEmpty(): Boolean = this != null && isNotEmpty()

/**
 * log
 */
fun loge(tag: String, message: String, e: Throwable? = null) = Logger.e(tag, message, e)

fun logd(tag: String, message: String, e: Throwable? = null) = Logger.d(tag, message, e)

fun logi(tag: String, message: String, e: Throwable? = null) = Logger.i(tag, message, e)

/**
 * presenter
 */
inline fun <reified T : AndroidViewModel> Fragment.getPresenter(): T = ViewModelProviders.of(this).get(T::class.java)

inline fun <reified T : AndroidViewModel> FragmentActivity.getPresenter(): T = ViewModelProviders.of(this).get(T::class.java)

/**
 * reg
 */
fun String?.isPhone(reg: String = RegExt.REG_PHONE): Boolean = RegExt.match(this, reg)

fun String?.isNumber(reg: String = RegExt.REG_NUMBER): Boolean = RegExt.match(this, reg)

fun String?.isDate(reg: String = RegExt.REG_DATE): Boolean = RegExt.match(this, reg)

fun String?.isDateTime(reg: String = RegExt.REG_DATE_TIME): Boolean = RegExt.match(this, reg)

fun String?.isReg(reg: String): Boolean = RegExt.match(this, reg)

/**
 * json
 */
//*************************************************************************************/
//********************************** String *******************************************/
//*************************************************************************************/

fun String?.stringToJsonObject(): JsonObject? =
        if (this == null) null else JsonExt.jsonParser.parse(this).asJsonObject

fun String?.toJsonArray(): JsonArray? =
        if (this == null) null else JsonExt.jsonParser.parse(this).asJsonArray

inline fun <reified T> String?.toList(): List<T>? =
        if (this == null) null else JsonExt.json.fromJson<List<T>>(JsonExt.jsonParser.parse(this), JsonExt.ArrayListType(T::class.java))

inline fun <reified T> String?.toLinkedList(): List<T>? =
        if (this == null) null else JsonExt.json.fromJson<List<T>>(JsonExt.jsonParser.parse(this), JsonExt.LinkedListType(T::class.java))

inline fun <reified T> String?.toObject(): T? =
        if (this == null) null else JsonExt.json.fromJson(this, T::class.java)

fun <T> String?.toList(clazz: Class<T>): List<T>? =
        if (this == null) null else JsonExt.json.fromJson<List<T>>(JsonExt.jsonParser.parse(this), JsonExt.ArrayListType(clazz))

fun <T> String?.toLinkedList(clazz: Class<T>): List<T>? =
        if (this == null) null else JsonExt.json.fromJson<List<T>>(JsonExt.jsonParser.parse(this), JsonExt.LinkedListType(clazz))

fun <T> String?.toObject(clazz: Class<T>): T? =
        if (this == null) null else JsonExt.json.fromJson(this, clazz)

//*************************************************************************************/
//**************************************** T ******************************************/
//*************************************************************************************/

fun <T> T?.toJsonObject(): JsonObject? =
        if (this == null) null else JsonExt.json.toJsonTree(this).asJsonObject

fun <T> T?.toJsonString(): String? = if (this == null) null else JsonExt.json.toJson(this)


//*************************************************************************************/
//*************************************** List ****************************************/
//*************************************************************************************/
fun List<*>?.toJsonArray(): JsonArray? =
        if (this == null) null else JsonExt.json.toJsonTree(this).asJsonArray

//*************************************************************************************/
//********************************** JsonArray ****************************************/
//*************************************************************************************/

inline fun <reified T> JsonArray?.toList(): List<T>? =
        if (this == null) null else JsonExt.json.fromJson(this, JsonExt.ArrayListType(T::class.java))

inline fun <reified T> JsonArray?.toLinkedList(): List<T>? =
        if (this == null) null else JsonExt.json.fromJson(this, JsonExt.LinkedListType(T::class.java))

fun <T> JsonArray?.toList(clazz: Class<T>): List<T>? =
        if (this == null) null else JsonExt.json.fromJson(this, JsonExt.ArrayListType(clazz))

fun <T> JsonArray?.toLinkedList(clazz: Class<T>): List<T>? =
        if (this == null) null else JsonExt.json.fromJson(this, JsonExt.LinkedListType(clazz))

//*************************************************************************************/
//********************************** JsonObject ***************************************/
//*************************************************************************************/

inline fun <reified T> JsonObject?.toObject(): T? =
        if (this == null) null else JsonExt.json.fromJson(this, T::class.java)

fun <T> JsonObject?.toObject(clazz: Class<T>): T? =
        if (this == null) null else JsonExt.json.fromJson(this, clazz)

fun <T> JsonObject.addObject(key: String, obj: T): JsonObject {
    this.add(key, obj.toJsonObject())
    return this
}

fun JsonObject.addArray(key: String, list: List<*>): JsonObject {
    this.add(key, list.toJsonArray())
    return this
}

/**
 * date
 */

fun Date?.formatDate(sdf: SimpleDateFormat = DateExt.sdfYMD()): String? = if (this == null) null else sdf.format(this)

fun Date?.formatDateTime(): String? = this.formatDate(DateExt.sdfYMDHMS())

fun String?.parseDate(sdf: SimpleDateFormat = DateExt.sdfYMD()): Date? = if (this == null || this.isEmpty()) null else sdf.parse(this)

fun String?.parseDateTime(): Date? = this.parseDate(DateExt.sdfYMDHMS())

fun Date?.clearTime(): Calendar? {
    return if (this == null) {
        return null
    } else Calendar.getInstance().also {
        it.time = this
        it.set(Calendar.HOUR_OF_DAY, 0)
        it.set(Calendar.MINUTE, 0)
        it.set(Calendar.SECOND, 0)
        it.set(Calendar.MILLISECOND, 0)
    }
}

/**
 * md5
 */
fun String?.md5(): String? {
    if (this == null || this.isEmpty())
        return null
    try {
        val messageDigest = MessageDigest.getInstance("md5")
        messageDigest.update(this.toByteArray())
        val bytes = messageDigest.digest()
        val stringBuffer = StringBuilder(2 * bytes.size)
        bytes.forEach {
            val x = it.toInt() and 0xff
            if (x <= 0xf) {
                stringBuffer.append(0)
            }
            stringBuffer.append(Integer.toHexString(x))
        }
        return stringBuffer.toString().toUpperCase()
    } catch (e: NoSuchAlgorithmException) {
        Log.e("Md5", "md5", e)
    }
    return null
}

/**
 * spf
 */

fun Context.spf(): SharedPreferences {
    return this.getSharedPreferences(AppInfo.preferencesName, 0)
}

fun Context.editor(): SharedPreferences.Editor {
    return spf().edit()
}

fun Context.spfBool(key: String): Boolean {
    return spf().getBoolean(key, false)
}

fun Context.spfBool(key: String, value: Boolean) {
    editor().putBoolean(key, value).apply()
}

fun Context.spfInt(key: String): Int {
    return spf().getInt(key, 0)
}

fun Context.spfInt(key: String, value: Int) {
    editor().putInt(key, value).apply()
}

fun Context.spfLong(key: String): Long {
    return spf().getLong(key, 0)
}

fun Context.spfLong(key: String, value: Long) {
    editor().putLong(key, value).apply()
}

fun Context.spfString(key: String): String? {
    return spf().getString(key, null)
}

fun Context.spfString(key: String, value: String) {
    editor().putString(key, value).apply()
}

fun Context.spfRemove(key: String) {
    editor().remove(key).apply()
}

fun Context.spfClear() {
    editor().clear().apply()
}
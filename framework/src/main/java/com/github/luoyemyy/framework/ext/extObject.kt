@file:Suppress("MemberVisibilityCanBePrivate")

package com.github.luoyemyy.framework.ext


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object DateExt {

    const val YMDHMS = "yyyy-MM-dd HH:mm:ss"
    const val YMD = "yyyy-MM-dd"

    fun sdfYMD() = SimpleDateFormat(YMD, Locale.getDefault())
    fun sdfYMDHMS() = SimpleDateFormat(YMDHMS, Locale.getDefault())

}

object JsonExt {

    val json: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
    val jsonParser = JsonParser()

    class ArrayListType constructor(private val clazz: Class<*>) : ParameterizedType {

        override fun getActualTypeArguments(): Array<Type> = arrayOf(clazz)

        override fun getRawType(): Type = ArrayList::class.java

        override fun getOwnerType(): Type? = null
    }

    class LinkedListType constructor(private val clazz: Class<*>) : ParameterizedType {

        override fun getActualTypeArguments(): Array<Type> = arrayOf(clazz)

        override fun getRawType(): Type = LinkedList::class.java

        override fun getOwnerType(): Type? = null
    }
}

object RegExt {

    const val REG_PHONE = "^1[3578]\\d{9}$"
    const val REG_NUMBER = "^\\d+$"
    const val REG_DATE = "^\\d{4}-\\d{1,2}-\\d{1,2}"
    const val REG_DATE_TIME = "^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}"

    internal fun match(string: String?, reg: String): Boolean = if (string.empty()) false else Pattern.compile(reg).matcher(string).matches()
}


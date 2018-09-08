package com.github.luoyemyy.framework.utils

import java.util.regex.Pattern

fun String?.isPhone(reg: String = RegUtils.REG_PHONE): Boolean = RegUtils.match(this, reg)

fun String?.isNumber(reg: String = RegUtils.REG_NUMBER): Boolean = RegUtils.match(this, reg)

fun String?.isDate(reg: String = RegUtils.REG_DATE): Boolean = RegUtils.match(this, reg)

fun String?.isDateTime(reg: String = RegUtils.REG_DATE_TIME): Boolean = RegUtils.match(this, reg)

fun String?.isReg(reg: String): Boolean = RegUtils.match(this, reg)

object RegUtils {

    val REG_PHONE = "^1[3578]\\d{9}$"
    val REG_NUMBER = "^\\d+$"
    val REG_DATE = "^\\d{4}-\\d{1,2}-\\d{1,2}"
    val REG_DATE_TIME = "^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}"

    internal fun match(string: String?, reg: String): Boolean = if (string.empty()) false else Pattern.compile(reg).matcher(string).matches()
}

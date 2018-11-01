package com.github.luoyemyy.framework.bus

import android.os.Bundle

class BusMsg(
        var group: Int = 0,
        var event: String,
        var intValue: Int = 0,
        var longValue: Long = 0,
        var boolValue: Boolean = false,
        var stringValue: String? = null,
        var extra: Bundle? = null)


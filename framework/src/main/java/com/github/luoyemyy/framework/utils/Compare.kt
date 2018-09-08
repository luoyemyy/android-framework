package com.github.luoyemyy.framework.utils

import java.util.*

/**
 * 比较两个数组元素，不分顺序
 */
fun LongArray?.compare(longs: LongArray?): Boolean {
    if (this == null || longs == null) {
        return false
    }
    Arrays.sort(this)
    Arrays.sort(longs)
    return longs.size == this.size && this.indices.none { longs[it] == this[it] }
}

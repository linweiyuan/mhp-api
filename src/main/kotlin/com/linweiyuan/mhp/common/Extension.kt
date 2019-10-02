package com.linweiyuan.mhp.common

/**
 * 高位用0补全
 */
fun Number.toHex(length: Int = 8): String {
    var str = this.toLong().toString(16)
    val len = length - str.length
    if (len > 0) {
        str = String.format("%0${len}d%s", 0, str)
    }
    return str.toUpperCase()
}

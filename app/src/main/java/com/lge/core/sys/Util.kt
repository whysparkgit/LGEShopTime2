package com.lge.core.sys

import android.content.res.TypedArray
import java.text.SimpleDateFormat
import java.util.*

class Util
{
    companion object {
        @JvmStatic
        fun Long.toDateFormat(strExp: String): String = SimpleDateFormat(strExp).format(Date(this))

        @JvmStatic
        fun GetNowDateFormat(strExp: String): String = SimpleDateFormat(strExp).format(Date())
    }
}
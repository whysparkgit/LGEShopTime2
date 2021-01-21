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

        @JvmStatic
        fun TypedArray.toIntArrayList(): ArrayList<Int> {
            val arrInt: ArrayList<Int> = ArrayList<Int>()

            for (i in 0 until this.length()) {
                arrInt.add(this.getResourceId(i, 0))
            }

            return arrInt
        }
    }
}
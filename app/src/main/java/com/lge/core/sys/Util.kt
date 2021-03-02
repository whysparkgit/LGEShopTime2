package com.lge.core.sys

import android.content.res.TypedArray
import com.lge.core.app.ApplicationProxy
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
        fun getDisplayWidthPx(): Int = ApplicationProxy.getContext().resources.displayMetrics.widthPixels

        @JvmStatic
        fun getDisplayHeightPx(): Int = ApplicationProxy.getContext().resources.displayMetrics.heightPixels

        @JvmStatic
        fun getDpToPx(dp: Int): Int = (dp * ApplicationProxy.getContext().resources.displayMetrics.density).toInt()

        @JvmStatic
        fun getPxToDp(px: Int): Int = (px / ApplicationProxy.getContext().resources.displayMetrics.density).toInt()
    }
}
package com.lge.core.sys

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
        fun getSpecificTime(nYear: Int, nMonth: Int, nDay: Int, nHour: Int, nMinute: Int, nSecond: Int): Long {
            val calendar = Calendar.getInstance()

            if (nYear != 0) calendar.add(Calendar.YEAR, nYear)

            if (nMonth != 0) calendar.add(Calendar.MONTH, nMonth)

            if (nDay != 0) calendar.add(Calendar.DAY_OF_YEAR, nDay)

            if (nHour != 0) calendar.add(Calendar.HOUR_OF_DAY, nHour)

            if (nMinute != 0) calendar.add(Calendar.MINUTE, nMinute)

            if (nSecond != 0) calendar.add(Calendar.SECOND, nSecond)

            return calendar.timeInMillis
        }

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
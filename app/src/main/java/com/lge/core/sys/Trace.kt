package com.lge.core.sys

import android.os.Environment
import android.util.Log
import com.lge.core.sys.Util.Companion.GetNowDateFormat
import java.io.File
import java.io.FileWriter
import java.io.IOException

class Trace {
    companion object {
        private const val LEVEL_ERROR = 0
        private const val LEVEL_WARN = 1
        private const val LEVEL_DEBUG = 2
        private const val LEVEL_INFO = 3

        private const val DUMP_FILE = false

        fun error(strMsg: String?) {
            if (Config.SUPPORT_DEBUG) logMessage(LEVEL_ERROR, strMsg)
        }

        fun warn(strMsg: String?) {
            if (Config.SUPPORT_DEBUG) logMessage(LEVEL_WARN, strMsg)
        }

        fun debug(strMsg: String?) {
            if (Config.SUPPORT_DEBUG) logMessage(LEVEL_DEBUG, strMsg)
        }

        fun info(strMsg: String?) {
            if (Config.SUPPORT_DEBUG) logMessage(LEVEL_INFO, strMsg)
        }

        fun dump(strMsg: String?) {
            if (Config.SUPPORT_DEBUG) logDump(strMsg)
        }

        @Synchronized
        fun logMessage(nLevel: Int, strMsg: String?) {
            if (strMsg.isNullOrEmpty()) return

            val strThreadName = Thread.currentThread().name
            var strFileName = Thread.currentThread().stackTrace[4].fileName
            val nLineNumber = Thread.currentThread().stackTrace[4].lineNumber

            if (strFileName.length > 20) {
                strFileName = strFileName.substring(0..20)
            }

            val strLog = String.format("%-20s %5d  %s\n", strFileName, nLineNumber, strMsg)

            when (nLevel) {
                LEVEL_ERROR -> Log.e(strThreadName, strLog)
                LEVEL_WARN -> Log.w(strThreadName, strLog)
                LEVEL_INFO -> Log.i(strThreadName, strLog)
                LEVEL_DEBUG -> Log.d(strThreadName, strLog)
                else -> Log.d(strThreadName, strLog)
            }

            if (DUMP_FILE) {
                logFile(strLog)
            }
        }

        @Synchronized
        fun logDump(strMsg: String?) {
            if (strMsg.isNullOrEmpty()) return

            val strThreadName = Thread.currentThread().name
            var strFileName = Thread.currentThread().stackTrace[6].fileName
            val nLineNumber = Thread.currentThread().stackTrace[6].lineNumber

            if (strFileName.length > 20) {
                strFileName = strFileName.substring(0..20)
            }

            var sbBuf: StringBuilder = StringBuilder()
            sbBuf.append(String.format("%-20s %5d  ", strFileName, nLineNumber))
            sbBuf.append("${strMsg}\n")

            Log.d(strThreadName, sbBuf.toString())

            if (DUMP_FILE) {
                logFile(sbBuf.toString())
            }
        }

        @Synchronized
        fun logFile(strMsg: String?) {
            if (strMsg.isNullOrEmpty()) return

            var objfile: FileWriter? = null
            val stFileName = "log"
            var stPath = "${Environment.getStorageDirectory().path}/Trace/log/"
            val dst = File(stPath)

            if (!dst.exists()) {
                if (!dst.mkdir()) {
                    return
                }
            }

            val stDate = GetNowDateFormat("yyyyMMdd")
            val stTime = GetNowDateFormat("HH:mm:ss")

            val bufLogPath = StringBuilder()
            bufLogPath.append("${stPath}${stFileName}_${stDate}.log")

            val bufLogMsg = java.lang.StringBuilder()
            bufLogMsg.append("[${stTime}] : $strMsg")

            try {
                objfile = FileWriter(bufLogPath.toString(), true)
                objfile.write(bufLogMsg.toString())
                objfile.write("\r\n")
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    objfile!!.close()
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }
        }
    }
}
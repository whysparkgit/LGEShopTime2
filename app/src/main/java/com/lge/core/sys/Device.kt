package com.lge.core.sys

import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import com.lge.core.app.ApplicationProxy
import com.lge.core.sys.Util.Companion.toDateFormat
import com.lge.shoptime.BuildConfig

class Device {
    class Connection {
        companion object {
            @JvmStatic
            fun isConnected() = getWiFiRSSILevel() > 10

            @JvmStatic
            fun getWiFiRSSILevel(): Int {
                val context = ApplicationProxy.getInstance().applicationContext
                val wifiManager: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo: WifiInfo = wifiManager.connectionInfo

                return wifiManager.calculateSignalLevel(wifiInfo.rssi)
            }
        }
    }

    class State {
        companion object {
            @JvmStatic
            fun getVersionName(): String = BuildConfig.VERSION_NAME

            @JvmStatic
            fun getBuildNumber(): String {
                return if (BuildConfig.BUILD_TYPE == "debug") {
                    "${getBuildDate()}D"
                } else {
                    "${getBuildDate()}R"
                }
            }

            @JvmStatic
            fun getBuildDate(): String = BuildConfig.BUILD_TIME.toDateFormat("yyyyMMddHHmm")
        }
    }
}
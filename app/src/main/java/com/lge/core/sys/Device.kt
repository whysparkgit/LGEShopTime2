package com.lge.core.sys

import android.content.Context
import android.content.res.Resources
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.lge.core.app.ApplicationProxy
import com.lge.core.sys.Util.Companion.toDateFormat
import com.lge.lgshoptimem.BuildConfig

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

            @JvmStatic
            fun printStatus() {
                Trace.debug(">> System Status :")
                Trace.debug(">> DEVICE = " + Build.DEVICE)
                Trace.debug(">> BRAND = " + Build.BRAND)
                Trace.debug(">> FINGERPRINT = " + Build.FINGERPRINT)
                Trace.debug(">> HARDWARE = " + Build.HARDWARE)
                Trace.debug(">> MANUFACTURER = " + Build.MANUFACTURER)
                Trace.debug(">> MODEL = " + Build.MODEL)
                Trace.debug(">> VERSION.CODENAME = " + Build.VERSION.CODENAME)
                Trace.debug(">> VERSION.BASE_OS = " + Build.VERSION.BASE_OS)
                Trace.debug(">> VERSION.RELEASE = " + Build.VERSION.RELEASE)
                Trace.debug(">> VERSION.INCREMENTAL = " + Build.VERSION.INCREMENTAL)
                Trace.debug(">> VERSION.PREVIEW_SDK_INT = " + Build.VERSION.PREVIEW_SDK_INT)
                Trace.debug(">> VERSION.SDK_INT = " + Build.VERSION.SDK_INT)

                Trace.debug(">> Network Status :")
                val wifiManager = ApplicationProxy.getInstance().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                Trace.debug(">> MacAddress = " + wifiManager.connectionInfo.macAddress)
                Trace.debug(">> Rssi = " + wifiManager.connectionInfo.rssi)
                Trace.debug(">> SSID = " + wifiManager.connectionInfo.ssid)
                Trace.debug(">> BSSID = " + wifiManager.connectionInfo.bssid)
                Trace.debug(">> IpAddress = " + wifiManager.connectionInfo.ipAddress)
                Trace.debug(">> Frequency = " + wifiManager.connectionInfo.frequency)
                Trace.debug(">> LinkSpeed = " + wifiManager.connectionInfo.linkSpeed)
                Trace.debug(">> NetworkId = " + wifiManager.connectionInfo.networkId)
//                Trace.debug(">> State.getMacAddress() = " + com.sdi.common.sys.Device.State.getMacAddr())
//                Trace.debug(">> State.getIpAddress() = " + com.sdi.common.sys.Device.State.getIpAddress())
//                Trace.debug(">> wifiManager.getWiFiRSSILevel() = " + com.sdi.common.sys.Device.Connection.getWiFiRSSILevel())

                Trace.debug(">> Display Status :")
                Trace.debug(">> widthPixels = ${Resources.getSystem().displayMetrics.widthPixels}")
                Trace.debug(">> heightPixels = ${Resources.getSystem().displayMetrics.heightPixels}")
                Trace.debug(">> density = ${Resources.getSystem().displayMetrics.density}")
                Trace.debug(">> dpi = ${Resources.getSystem().displayMetrics.density * 160f}")

                Trace.debug(">> Application Status :")
                Trace.debug(">> VERSION_NAME = " + BuildConfig.VERSION_NAME)
                Trace.debug(">> VERSION_CODE = " + BuildConfig.VERSION_CODE)
                Trace.debug(">> APPLICATION_ID = " + BuildConfig.APPLICATION_ID)
                Trace.debug(">> DEBUG = " + BuildConfig.DEBUG)
                Trace.debug(">> BUILD_TYPE = " + BuildConfig.BUILD_TYPE)
            }
        }
    }
}
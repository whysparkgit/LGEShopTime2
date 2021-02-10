package com.lge.lgshoptimem.model.http

class CommonHeader private constructor()
{
    companion object {
        const val HDR_AUTH_KEY = "lgsp_auth"
        const val HDR_APP_ID = "app_id"
        const val HDR_APP_VER = "app_ver"
        const val HDR_DEVICE = "dvc_id"
        const val HDR_COUNTRY = "cntry_cd"
        const val HDR_PRODUCT = "prod_cd"
        const val HDR_PLATFORM = "plat_cd"
        const val HDR_LANGUAGE = "lang_cd"
        const val HDR_OS_VER = "os_ver"
        const val HDR_SDK_VER = "sdk_ver"
        const val HDR_PUBLISH = "publish_flag"

        private var mInstance: CommonHeader? = null

        @JvmStatic
        fun getInstance(): CommonHeader = mInstance?:
            synchronized(this) {
                mInstance?: CommonHeader().also {
                    mInstance = it
                }
            }
    }

    fun getHeaders(): MutableMap<String, String> {
        val headers: MutableMap<String, String> = mutableMapOf()

        return headers.apply {
            put(HDR_AUTH_KEY, "5/ZrQJWkh2GwaZ0/zDHcqjEqrHxs8du/xZFxtUIJIIa9jhoYo916T5sflL8aqlBETzRUdwhfY0wWvw7gkkShdboXIwtJwnCclA/P8uJqm0Mp7gMK73pCcuXvZBauC63ZGbUT1Q==")
            put(HDR_APP_ID, "com.lgshop.app")
            put(HDR_APP_VER, "1.0.0")
            put(HDR_DEVICE, "testmobileid")
            put(HDR_COUNTRY, "US")
            put(HDR_PRODUCT, "webOSTV 5.0")
            put(HDR_PLATFORM, "W20H")
            put(HDR_LANGUAGE, "en-US")
            put(HDR_OS_VER, "3.0")
            put(HDR_SDK_VER, "1.0.0")
            put(HDR_PUBLISH, "Y")
        }
    }
}
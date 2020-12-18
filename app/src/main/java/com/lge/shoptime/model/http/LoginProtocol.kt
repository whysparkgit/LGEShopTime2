package com.lge.shoptime.model.http

import com.lge.core.net.AbstractHttpProtocol
import com.lge.core.sys.Const
import com.lge.shoptime.model.dto.Login

class LoginProtocol : AbstractHttpProtocol<Login.Response>()
{
    val PATH = "device/auth.lge"

    override fun getUrl(): String = getDomain() + Const.NETWORK.DEV_SERVER.PATH + PATH

    override fun getRequestHeaderMap(): Map<String, String> {
        addRequestHeader("lgsp_auth", "5/ZrQJWkh2GwaZ0/zDHcqjEqrHxs8du/xZFxtUIJIIa9jhoYo916T5sflL8aqlBETzRUdwhfY0wWvw7gkkShdboXIwtJwnCclA/P8uJqm0Mp7gMK73pCcuXvZBauC63ZGbUT1Q==")
        addRequestHeader("app_id", "com.lgshop.app")
        addRequestHeader("app_ver", "1.0.0")
        addRequestHeader("dvc_id", "testmobileid")
        addRequestHeader("cntry_cd", "US")
        addRequestHeader("prod_cd", "webOSTV 5.0")
        addRequestHeader("plat_cd", "W20H")
        addRequestHeader("lang_cd", "en-US")
        addRequestHeader("os_ver", "3.0")
        addRequestHeader("sdk_ver", "1.0.0")
        addRequestHeader("publish_flag", "Y")

        return super.getRequestHeaderMap()
    }
}
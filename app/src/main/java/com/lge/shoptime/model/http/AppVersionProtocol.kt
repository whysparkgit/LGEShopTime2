package com.lge.shoptime.model.http

import com.lge.core.net.AbstractHttpProtocol
import com.lge.core.sys.Const
import com.lge.shoptime.model.dto.AppVersionList
import com.lge.shoptime.model.dto.Login

class AppVersionProtocol : AbstractHttpProtocol<AppVersionList>()
{
    val PATH = "device/app.version"

    override fun getUrl(): String = getDomain() + Const.NETWORK.DEV_SERVER.PATH + PATH
}
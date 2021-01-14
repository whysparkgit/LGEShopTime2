package com.lge.lgshoptimem.model.http

import com.lge.core.net.AbstractHttpProtocol
import com.lge.core.net.HttpConst
import com.lge.core.sys.Const
import java.io.File

class FileDownloadProtocol : AbstractHttpProtocol<File>()
{
    val PATH = "device/file.id"

    lateinit var mstrFileUrl: String
    lateinit var mstrFileId: String

    fun setUrl(strUrl: String) {
        mstrFileUrl = strUrl
    }

    fun setFileId(strFileId: String) {
        mstrFileId = strFileId
    }

    override fun getUrl(): String {
        return if (HttpConst.HTTP_FEATURE_DOWNLOAD_URL) {
            mstrFileUrl
        } else {
            getDomain() + Const.NETWORK.DEV_SERVER.PATH + PATH + "/${mstrFileId}"
        }
    }
}
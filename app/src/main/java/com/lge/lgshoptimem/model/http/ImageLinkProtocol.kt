package com.lge.lgshoptimem.model.http

import android.graphics.Bitmap
import com.lge.core.net.AbstractHttpProtocol

class ImageLinkProtocol : AbstractHttpProtocol<Bitmap>()
{
    lateinit var mstrImageUrl: String

    override fun getUrl(): String = mstrImageUrl

    fun setUrl(strUrl: String) {
        mstrImageUrl = strUrl
    }
}
package com.lge.lgshoptimem.model.http

import com.lge.core.net.AbstractHttpProtocol
import com.lge.core.net.HttpConst
import com.lge.lgshoptimem.model.dto.BaseResponse
import com.lge.lgshoptimem.model.dto.CategoryList
import com.lge.lgshoptimem.model.dto.FavoriteKeywordList
import com.lge.lgshoptimem.model.dto.KeywordList

class AddFavoriteKeywordProtocol : AbstractHttpProtocol<BaseResponse>()
{
    val PATH = "mypage/keyword.lge"

    override fun getUrl() = getDomain() + getPath() + PATH

    override fun getRequestHeaderMap(): Map<String, String> {
        return super.getRequestHeaderMap().run {
            CommonHeader.getInstance().getHeaders()
        }
    }

    override fun getMethod() = HttpConst.HTTP_POST

    override fun getContentType(): String = HttpConst.HTTP_MIME_TYPE_TEXT

    fun setRequestBody(arrKeyword: ArrayList<String>) {
        var strKeywords: String = ""

        arrKeyword.forEach {
            strKeywords += "$it,"
        }

        strKeywords = strKeywords.substring(0, strKeywords.lastIndex)

        setRequestBody(strKeywords)
    }
}
package com.lge.lgshoptimem.model.http

import com.lge.core.net.AbstractHttpProtocol
import com.lge.lgshoptimem.model.dto.CategoryList
import com.lge.lgshoptimem.model.dto.FavoriteKeywordList
import com.lge.lgshoptimem.model.dto.KeywordList

class FavoriteKeywordListProtocol : AbstractHttpProtocol<FavoriteKeywordList.Response>()
{
    val PATH = "mypage/alert/keyword.lge"

    override fun getUrl() = getDomain() + getPath() + PATH

    override fun getRequestHeaderMap(): Map<String, String> {
        return super.getRequestHeaderMap().run {
            CommonHeader.getInstance().getHeaders()
        }
    }
}
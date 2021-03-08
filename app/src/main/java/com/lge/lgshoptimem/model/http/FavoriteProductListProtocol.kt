package com.lge.lgshoptimem.model.http

import com.lge.core.net.AbstractHttpProtocol
import com.lge.lgshoptimem.model.dto.CategoryList
import com.lge.lgshoptimem.model.dto.FavoriteKeywordList
import com.lge.lgshoptimem.model.dto.FavoriteProductList
import com.lge.lgshoptimem.model.dto.KeywordList

class FavoriteProductListProtocol : AbstractHttpProtocol<FavoriteProductList.Response>()
{
    val PATH = "mypage/favorite.lge"

    override fun getUrl() = getDomain() + getPath() + PATH

    override fun getRequestHeaderMap(): Map<String, String> {
        return super.getRequestHeaderMap().run {
            CommonHeader.getInstance().getHeaders()
        }
    }
}
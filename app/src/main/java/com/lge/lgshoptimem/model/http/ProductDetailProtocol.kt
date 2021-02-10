package com.lge.lgshoptimem.model.http

import com.lge.core.net.AbstractHttpProtocol
import com.lge.core.sys.Const
import com.lge.lgshoptimem.model.dto.CurationList
import com.lge.lgshoptimem.model.dto.ProductDetail
import com.lge.lgshoptimem.model.dto.WatchNow

class ProductDetailProtocol : AbstractHttpProtocol<ProductDetail.Response>()
{
    val PATH = "main/category/product/detail.lge"

    override fun getUrl() = getDomain() + getPath() + PATH + getQueries()

    override fun getRequestHeaderMap(): Map<String, String> {
        return super.getRequestHeaderMap().run {
            CommonHeader.getInstance().getHeaders()
        }
    }

    fun setPartnerId(strId: String) = addQuery("patnrId", strId)

    fun setProductId(strId: String) = addQuery("prdtId", strId)

    fun setCurationId(strId: String) = addQuery("curationId", strId)
}
package com.lge.lgshoptimem.model.http

import com.lge.core.net.AbstractHttpProtocol
import com.lge.lgshoptimem.model.dto.CategoryList

class CategoryListProtocol : AbstractHttpProtocol<CategoryList.Response>()
{
    val PATH = "main/category.lge"

    override fun getUrl() = getDomain() + getPath() + PATH

    override fun getRequestHeaderMap(): Map<String, String> {
        return super.getRequestHeaderMap().run {
            CommonHeader.getInstance().getHeaders()
        }
    }
}
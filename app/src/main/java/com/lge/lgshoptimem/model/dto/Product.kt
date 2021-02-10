package com.lge.lgshoptimem.model.dto

open class Product {
    val prdtId: String = ""
    val prdtNm: String = ""
    val priceInfo: String = ""
    val prdtFavorCnt: String = ""
    val imgUrl: String = ""
    val revwGrd: String = ""


    open fun getProductThumbnail(): String {
        return imgUrl
    }

    open fun isSoldOut(): Boolean {
        return false
    }

//    fun getPrice(nIndex: Int): String {
//        if (priceInfo.isNullOrEmpty()) return ""
//
//        val arrPrices = priceInfo.split("|")
//
//        if (arrPrices.size <= nIndex) return ""
//
//        return arrPrices[nIndex]
//    }

    open fun getOriginPrice(): String {
        if (priceInfo.isNullOrEmpty()) return ""

        val arrPrices = priceInfo.split("|")

        if (arrPrices.isEmpty()) return ""

        return arrPrices[0]
    }

    open fun getShopTimePrice(): String {
        if (priceInfo.isNullOrEmpty()) return ""

        val arrPrices = priceInfo.split("|")

        if (arrPrices.size < 2) return ""

        return arrPrices[1]
    }

    open fun getDiscount(): String {
        if (priceInfo.isNullOrEmpty()) return ""

        val arrPrices = priceInfo.split("|")

        if (arrPrices.size < 5) return ""

        return arrPrices[4]
    }
}
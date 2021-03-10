package com.lge.lgshoptimem.model.dto

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.Bindable
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.ui.common.FavoriteProductManager

open class Product() : Parcelable {
    var prdtId: String = ""
    var prdtNm: String = ""
        get() { // fixme
            return prdtId
        }
    var priceInfo: String = ""
    var prdtFavorCnt: String = ""
    var imgUrl: String = ""
    var revwGrd: String = ""
    var patnrId: String = ""
    var patncLogoPath: String = ""
    var bOrigin: Boolean = false
    var bFavorite: Boolean = false

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this() {
        parcel.run {
            prdtId = readString().toString()
            prdtNm = readString().toString()
            priceInfo = readString().toString()
            prdtFavorCnt = readString().toString()
            imgUrl = readString().toString()
            revwGrd = readString().toString()
            patnrId = readString().toString()
            patncLogoPath = readString().toString()
            bFavorite = (readInt() != 0)
            bOrigin = (readInt() != 0)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.run {
            writeString(prdtId)
            writeString(prdtNm)
            writeString(priceInfo)
            writeString(prdtFavorCnt)
            writeString(imgUrl)
            writeString(revwGrd)
            writeString(patnrId)
            writeString(patncLogoPath)
            writeInt(if (bFavorite) 1 else 0)
            writeInt(if (bOrigin) 1 else 0)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    open fun getProductThumbnail(): String {
        return imgUrl
    }

    open fun isSoldOut(): Boolean {
        return false
    }

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

    fun getReviewGrade(): Float {
        if (revwGrd.isNullOrEmpty()) return 0f

        if (revwGrd == "null") return 0f

        return revwGrd.toFloat()
    }

    fun getFavorite() = FavoriteProductManager.getInstance().isFavoriteChecked(prdtId)

    fun setFavorite(checked: Boolean) {
        bFavorite = checked
    }
}
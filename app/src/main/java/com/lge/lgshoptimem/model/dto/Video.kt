package com.lge.lgshoptimem.model.dto

import android.os.Parcel
import android.os.Parcelable
import com.lge.core.sys.Trace

open class Video() : ChannelIcon(false, 0, ""), Parcelable {
    var showId: String = ""
    var showNm: String = ""
    var showUrl: String = ""
    var patnrId: String = ""
    var patncNm: String = ""
    var patncLogoPath: String = ""
    var catCd: String = ""
    var catNm: String = ""
    var orderPhnNo: String = ""
    var expsOrd: String = ""
    var thumbnailUrl: String = ""
    var showSubtitlUrl: String = ""
    var liveFlag: String = "N"
    var productInfos: ArrayList<Product> = arrayListOf()
    var fromDetail: Int = 0

    companion object CREATOR : Parcelable.Creator<Video> {
        override fun createFromParcel(parcel: Parcel): Video {
            return Video(parcel)
        }

        override fun newArray(size: Int): Array<Video?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this() {
        parcel.run {
            showId = readString().toString()
            showNm = readString().toString()
            showUrl = readString().toString()
            patnrId = readString().toString()
            patncNm = readString().toString()
            patncLogoPath = readString().toString()
            catCd = readString().toString()
            catNm = readString().toString()
            orderPhnNo = readString().toString()
            expsOrd = readString().toString()
            thumbnailUrl = readString().toString()
            showSubtitlUrl = readString().toString()
            liveFlag = readString().toString()
            productInfos = arrayListOf<Product>().apply {
                readList(this, Product::class.java.classLoader)
            }
            fromDetail = readInt()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.run {
            writeString(showId)
            writeString(showNm)
            writeString(showUrl)
            writeString(patnrId)
            writeString(patncNm)
            writeString(patncLogoPath)
            writeString(catCd)
            writeString(catNm)
            writeString(orderPhnNo)
            writeString(expsOrd)
            writeString(thumbnailUrl)
            writeString(showSubtitlUrl)
            writeString(liveFlag)
            writeList(productInfos)
            writeInt(fromDetail)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    open fun isLive(): Boolean {
        Trace.debug(">> isLive() = $liveFlag")
        return (liveFlag == "Y")
    }

    override fun getChannelIconUrl() = patncLogoPath
}

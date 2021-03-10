package com.lge.lgshoptimem.ui.component

import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Paint
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.ViewDataBinding
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.BR
import com.lge.lgshoptimem.model.dto.Product
import com.lge.lgshoptimem.model.dto.Show
import com.lge.lgshoptimem.model.http.ImageLinkProtocol
import com.lge.lgshoptimem.ui.common.FavoriteProductManager
import com.lge.lgshoptimem.ui.common.UpcomingAlarmManager
import java.util.*

object CommonBindingAdapter
{
    @JvmStatic
    fun TypedArray.toIntArrayList(): ArrayList<Int> {
        val arrInt: ArrayList<Int> = ArrayList<Int>()

        for (i in 0 until this.length()) {
            arrInt.add(this.getResourceId(i, 0))
        }

        return arrInt
    }

    @JvmStatic
    @BindingAdapter("position")
    fun setPosition(comp: BaseListComponent, position: Int) {
        comp.setPosition(position)
    }

    @JvmStatic
    @BindingAdapter("resource")
    fun setResource(imageView: ImageView, resourceId: Int) {
        imageView.setImageResource(resourceId)
    }

    @JvmStatic
    @BindingAdapter("strikeThrough")
    fun strikeThrough(textView: TextView, bFlag: Boolean) {
        if (bFlag) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageUrl(imageView: ImageView, strUrl: String?) {
        Trace.debug("++ setImageUrl() strUrl = $strUrl")
        Trace.debug("++ setImageUrl() imageView = $imageView")

        if (!strUrl.isNullOrEmpty()) {
            requestImage(strUrl, imageView)
        }
    }

    private fun requestImage(url: String, imageView: ImageView) {
        val protocol: ImageLinkProtocol = ProtocolFactory.create(ImageLinkProtocol::class.java)
        protocol.setUrl(url)

        protocol.setHttpResponsable(object : HttpResponsable<Bitmap> {
            override fun onResponse(bitmap: Bitmap) {
                Trace.debug(">> requestImage() onResponse()")
                imageView.setImageBitmap(bitmap)
                imageView.invalidate()
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestImage() onFailure($nError) strMsg = $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }

    @JvmStatic
    @BindingAdapter("android:checked", "app:viewdata") //, requireAll = false)
    fun setFavorite(view: CompoundButton, bFavorite: Boolean, product: Product) {
        view.isChecked = bFavorite
        view.setTag(view.id, product)
//        Trace.debug("++ BindingAdapter.setFavorite() bFavorite = $bFavorite productId = ${product.prdtId}")
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "android:checked")
    fun getFavorite(view: CompoundButton): Boolean {
        if (view.getTag(view.id) == null) return view.isChecked

        val product: Product = view.getTag(view.id) as Product
        Trace.debug("++ BindingAdapter.getFavorite() view.id = ${view.id} isChecked = ${view.isChecked} productId = ${product.prdtId}")

        // TODO set favorite to FM
        if (view.isChecked) {
            FavoriteProductManager.getInstance().addFavorite(product)
        } else {
            FavoriteProductManager.getInstance().deleteFavorite(product)
        }

        return view.isChecked
    }

    @JvmStatic
    @BindingAdapter("android:checked", "app:viewdata") //, requireAll = false)
    fun setAlarm(view: CompoundButton, bAlarm: Boolean, show: Show) {
        view.isChecked = bAlarm
        view.setTag(view.id, show)
//        Trace.debug("++ BindingAdapter.setAlarm() bAlarm = $bAlarm showId = ${show.showId}")
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "android:checked")
    fun getAlarm(view: CompoundButton): Boolean {
        if (view.getTag(view.id) == null) return view.isChecked

        val show: Show = view.getTag(view.id) as Show
        Trace.debug("++ BindingAdapter.getAlarm() view.id = ${view.id} isChecked = ${view.isChecked} showId = ${show.showId}")

        // TODO set favorite to FM
        if (view.isChecked) {
            UpcomingAlarmManager.getInstance().addAlarm(show)
        } else {
            UpcomingAlarmManager.getInstance().deleteAlarm(show)
        }

        return view.isChecked
    }
}
package com.lge.lgshoptimem.ui.component

import android.graphics.Bitmap
import android.graphics.Paint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.BR
import com.lge.lgshoptimem.model.http.ImageLinkProtocol

object CommonBindingAdapter
{
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
                Trace.debug(">> requestImage() onFailure()")
            }
        })

        NetworkManager.getInstance().asyncRequest(protocol)
    }
}
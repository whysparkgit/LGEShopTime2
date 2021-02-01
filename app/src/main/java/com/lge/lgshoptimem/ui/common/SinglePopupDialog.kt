package com.lge.lgshoptimem.ui.common

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.lge.core.app.ApplicationProxy
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R

class SinglePopupDialog private constructor(builder: Builder): DialogFragment(), View.OnClickListener
{
    private var mstrTitle: String? = null
    private var mstrMessage: String? = null
    private var mstrNeutral: String? = null
    private var mstrPositive: String? = null
    private var mstrNegative: String? = null
    private var mbCancelable = true

    private var mNeutralListener: DialogInterface.OnClickListener? = null
    private var mPositiveListener: DialogInterface.OnClickListener? = null
    private var mNegativeListener: DialogInterface.OnClickListener? = null

    init {
        setBuilder(builder)
    }

    fun setBuilder(builder: Builder) {
        mstrTitle = builder.mstrTitle
        mstrMessage = builder.mstrMessage
        mstrNeutral = builder.mstrNeutral
        mstrPositive = builder.mstrPositive
        mstrNegative = builder.mstrNegative
        mbCancelable = builder.mbCancelable
        mNeutralListener = builder.mNeutralListener
        mPositiveListener = builder.mPositiveListener
        mNegativeListener = builder.mNegativeListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Trace.debug("++ onCreateView()")
        val view: View = inflater.inflate(R.layout.dialog_popup, container)

        val window = dialog!!.window
        window!!.setBackgroundDrawableResource(R.drawable.bg_rect_fill_white_r20)

        val windowParams = window.attributes
        windowParams.dimAmount = 0.50f
        windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window.attributes = windowParams

        init(view)

        return view
    }

    fun init(view: View) {
        val tvTitle = view.findViewById<TextView>(R.id.dg_tv_title)
        val tvMessage = view.findViewById<TextView>(R.id.dg_tv_message)
        val llSelectioin = view.findViewById<LinearLayout>(R.id.dg_ll_selection)
        val tvNeutral = view.findViewById<TextView>(R.id.dg_tv_neutral)
        val tvPositive = view.findViewById<TextView>(R.id.dg_tv_positive)
        val tvNegative = view.findViewById<TextView>(R.id.dg_tv_negative)

        tvTitle.text = mstrTitle
        tvMessage.text = mstrMessage
        tvNeutral.text = mstrNeutral
        tvPositive.text = mstrPositive
        tvNegative.text = mstrNegative
        isCancelable = mbCancelable

        if (mNeutralListener != null) {
            tvNeutral.visibility = View.VISIBLE
            tvNeutral.setOnClickListener(this)
            llSelectioin.visibility = View.GONE
        }

        if (mPositiveListener != null) {
            llSelectioin.visibility = View.VISIBLE
            tvPositive.setOnClickListener(this)
            tvNeutral.visibility = View.GONE
        }

        if (mNegativeListener != null) {
            llSelectioin.visibility = View.VISIBLE
            tvNegative.setOnClickListener(this)
            tvNeutral.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dg_tv_neutral -> if (mNeutralListener != null) {
                mNeutralListener!!.onClick(dialog, R.id.dg_tv_neutral)
            }
            R.id.dg_tv_negative -> if (mNegativeListener != null) {
                mNegativeListener!!.onClick(dialog, R.id.dg_tv_negative)
            }
            R.id.dg_tv_positive -> if (mPositiveListener != null) {
                mPositiveListener!!.onClick(dialog, R.id.dg_tv_positive)
            }
        }
    }

    class Builder private constructor() {
        var mstrTitle: String? = null
        var mstrMessage: String? = null
        var mstrNeutral: String? = null
        var mstrPositive: String? = null
        var mstrNegative: String? = null
        var mbCancelable = true
        var mNeutralListener: DialogInterface.OnClickListener? = null
        var mPositiveListener: DialogInterface.OnClickListener? = null
        var mNegativeListener: DialogInterface.OnClickListener? = null
        private var mDialog: SinglePopupDialog? = null

        companion object {
            private val mBuilder = Builder()

            fun create(): Builder {
                mBuilder.init()
                return mBuilder
            }
        }

        private fun init() {
            mstrTitle = null
            mstrMessage = null
            mstrNeutral = null
            mstrPositive = null
            mstrNegative = null
            mbCancelable = true
            mNeutralListener = null
            mPositiveListener = null
            mNegativeListener = null
        }

        fun setTitle(strTitle: String?): Builder {
            mstrTitle = strTitle
            return this
        }

        fun setMessage(strMsg: String?): Builder {
            mstrMessage = strMsg
            return this
        }

        fun setNeutralButton(strBtn: String?, listener: DialogInterface.OnClickListener?): Builder {
            mstrNeutral = strBtn
            mNeutralListener = listener
            return this
        }

        fun setPositiveButton(strBtn: String?, listener: DialogInterface.OnClickListener?): Builder {
            mstrPositive = strBtn
            mPositiveListener = listener
            return this
        }

        fun setNegativeButton(strBtn: String?, listener: DialogInterface.OnClickListener?): Builder {
            mstrNegative = strBtn
            mNegativeListener = listener
            return this
        }

        fun setCancelable(bCancelable: Boolean): Builder {
            mbCancelable = bCancelable
            return this
        }

        fun build(): SinglePopupDialog {
            if (mDialog == null) {
                mDialog = SinglePopupDialog(this)
            }
            return mDialog!!
        }

        @JvmOverloads
        fun show(fragmentManager: FragmentManager? = ApplicationProxy.getInstance().getActivity()?.getSupportFragmentManager()): SinglePopupDialog {
            if (mDialog == null) {
                mDialog = SinglePopupDialog(this)
                mDialog!!.show(fragmentManager!!, null)
                return mDialog!!
            }

            mDialog!!.setBuilder(this)

            if (mDialog?.dialog == null) {
                return if (mDialog!!.isAdded) {
                    mDialog!!
                } else {
                    mDialog!!.show(fragmentManager!!, null)
                    mDialog!!
                }
            }

            if (mDialog!!.dialog!!.isShowing) {
                mDialog!!.view?.let { mDialog!!.init(it) }
            } else {
                mDialog!!.show(fragmentManager!!, null)
            }

            return mDialog!!
        }

        fun destroy() {
            if (mDialog == null) return
            mDialog!!.dismiss()
            mDialog = null
            init()
        }
    }
}
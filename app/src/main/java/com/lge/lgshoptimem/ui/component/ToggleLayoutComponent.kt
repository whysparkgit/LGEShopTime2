package com.lge.lgshoptimem.ui.component

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.CompToggleLayoutBinding

class ToggleLayoutComponent @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0):
        LinearLayout (context, attrs, defStyleAttr), View.OnClickListener
{

    var mIcon: Int = 0
    val mBinding: CompToggleLayoutBinding
    var mbToggleOn = false
    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.ToggleLayoutComponentAttrs, 0,0).apply {
            mIcon = getResourceId(R.styleable.ToggleLayoutComponentAttrs_imgResource,0)
            Trace.debug(">> mIcon : $mIcon")
        }

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.comp_toggle_layout, this, true)
        mBinding.listener = this
        setOnClickListener(this)
        mBinding.compIvIcon.text = "asdasdasd"

//        mBinding.compIvIcon.setImageResource(mIcon)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Trace.debug("++ onLayout() l=$l t=$t r=$r b=$b root = ${mBinding.root}")
        mBinding.root.layout(l, t, r, b)
    }

    override fun onClick(v: View) {
        Trace.debug("++ onClick() v = ${v.id}")

        if (mbToggleOn) {
            Trace.debug("++ onClick() mbToggleOn = false")
            mbToggleOn = false
            v.setBackgroundResource(R.drawable.bg_rect_border_grey_r2)

        } else {
            Trace.debug("++ onClick() mbToggleOn = true")
            mbToggleOn = true
            v.setBackgroundResource(R.drawable.bg_rect_border_pink_r2)

        }

//        mItemListener?.onItemClick(mBinding.root, mnPosition, v, pos)
    }



}
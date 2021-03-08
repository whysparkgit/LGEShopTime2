package com.lge.lgshoptimem.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.CompToggleLayoutBinding

class ToggleLayoutComponent @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0):
        ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener
{

    var mIcon: Int = 0
    val mBinding: CompToggleLayoutBinding
    var mbToggleOn = false

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.ToggleLayoutComponentAttrs, 0,0).apply {
            mIcon = getResourceId(R.styleable.ToggleLayoutComponentAttrs_imgResource,0)
            Trace.debug(">> mIcon : $mIcon")
            recycle()
        }

        mBinding = CompToggleLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        mBinding.listener = this
        mBinding.compIvIcon.setImageResource(mIcon)
    }

    override fun onFinishInflate() {
        Trace.debug("++ onFinishInflate()")
        super.onFinishInflate()

        setBackgroundResource(R.drawable.bg_rect_border_grey_r2)
    }

    override fun onClick(v: View) {
        Trace.debug("++ onClick() v = ${v.id} mbToggleOn = $mbToggleOn")
        mbToggleOn = !mbToggleOn

        if (mbToggleOn) {
            setBackgroundResource(R.drawable.bg_rect_border_grey_r2)
        } else {
            setBackgroundResource(R.drawable.bg_rect_border_pink_r2)
        }
    }
}
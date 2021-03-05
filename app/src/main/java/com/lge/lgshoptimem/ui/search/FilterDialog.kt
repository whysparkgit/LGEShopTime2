package com.lge.lgshoptimem.ui.search

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.DialogFilterBinding


class FilterDialog : DialogFragment() {
    private lateinit var mBinding: DialogFilterBinding

    val liveChannels = arrayOf(false,false,false,false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DialogFilterBinding.inflate(inflater)
        mBinding.listener = this

        val mDialog = dialog
        mDialog!!.setCanceledOnTouchOutside(false)
        mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        mDialog.window!!.setGravity(Gravity.BOTTOM)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.dfTb1.setOnCheckedChangeListener(ToggleCheckedChangeListener())
        mBinding.dfTb2.setOnCheckedChangeListener(ToggleCheckedChangeListener())
        mBinding.dfTb3.setOnCheckedChangeListener(ToggleCheckedChangeListener())
        mBinding.dfTb4.setOnCheckedChangeListener(ToggleCheckedChangeListener())
        mBinding.dfTbAll.setOnCheckedChangeListener(ToggleCheckedChangeListener())

    }

    inner class ToggleCheckedChangeListener: CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(view : CompoundButton?, isChecked: Boolean) {
            when (view?.id) {
                R.id.df_tb_all -> {
                    if(isChecked) {
                        mBinding.dfTb1.isSelected = true
                        liveChannels[0] = true
                        liveChannels[1] = true
                        liveChannels[2] = true
                        liveChannels[3] = true
                    } else {
                        liveChannels[0] = false
                        liveChannels[1] = false
                        liveChannels[2] = false
                        liveChannels[3] = false
                    }
                }
                R.id.df_tb_1 -> {
                    liveChannels[0] = isChecked
                }
                R.id.df_tb_2 -> {
                    liveChannels[1] = isChecked
                }
                R.id.df_tb_3 -> {
                    liveChannels[2] = isChecked
                }
                R.id.df_tb_4 -> {
                    liveChannels[3] = isChecked
                }
            }
        }
    }

    fun onClick(view: View) {
        when(view.id) {
            R.id.df_ib_back -> {
                dismiss()
                Trace.debug(">> isFilter Back button Clicked ")
                Trace.debug(">>exit filter ${liveChannels[0]}")
            }
        }
    }

    override fun onResume() {
        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        super.onResume()
    }

}

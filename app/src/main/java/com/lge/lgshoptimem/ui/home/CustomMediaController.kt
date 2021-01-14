package com.lge.lgshoptimem.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.MediaController
import android.widget.SeekBar
import com.lge.lgshoptimem.databinding.CompMediaControlBinding

class CustomMediaController(context: Context): MediaController(context)
{
    private lateinit var mBinding: CompMediaControlBinding

    override fun setAnchorView(view: View?) {
        super.setAnchorView(view)
        removeAllViews()

        mBinding = CompMediaControlBinding.inflate(LayoutInflater.from(context))

        mBinding.sbProgress.apply {
            max = 1000
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (!fromUser) return


                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    TODO("Not yet implemented")
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    TODO("Not yet implemented")
                }
            })
        }


        addView(mBinding.root)
    }
}

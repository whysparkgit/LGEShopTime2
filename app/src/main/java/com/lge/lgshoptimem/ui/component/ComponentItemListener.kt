package com.lge.lgshoptimem.ui.component

import android.view.View

interface ComponentItemListener {
    fun onClick(v: View, pos: Int)

    fun onItemClick(parent: View, parentPos: Int, item: View, pos: Int)
}
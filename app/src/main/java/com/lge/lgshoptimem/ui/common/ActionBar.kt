package com.lge.lgshoptimem.ui.common

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.lge.core.app.ApplicationProxy
import com.lge.lgshoptimem.R

class ActionBar(var listener: onActionBarListener)
{
    companion object {
        const val ACTION_NONE = 0
        const val ACTION_BACK = 1
        const val ACTION_CLOSE = 2
        const val ACTION_MENU = 3
        const val ACTION_NOTI = 4
        const val ACTION_INFO = 5

        const val ALIGN_LEFT = 5
        const val ALIGN_CENTER = 0
        const val ALIGN_RIGHT = 6
    }

    var darkMode = false
    var title: String? = null
    var leftBtn = 0
    var rightBtn = 0
    var mCount = 0
    var mTitleAlign = ALIGN_CENTER

    private var actionBarListener: onActionBarListener? = null

    interface onActionBarListener {
        fun onLeft()
        fun onRight()
    }

    init {
        actionBarListener = listener
    }

    fun setCount(count: Int) {
        mCount = count
    }

    fun setAlign(align: Int) {
        mTitleAlign = align
    }

    fun onClickLeft(view: View?) {
        if (actionBarListener != null) {
            if (leftBtn != ACTION_NONE) {
                actionBarListener!!.onLeft()
            }
        }
    }

    fun onClickRight(view: View?) {
        if (actionBarListener != null) {
            if (rightBtn != ACTION_NONE) {
                actionBarListener!!.onRight()
            }
        }
    }

    fun setButton(left: Int, right: Int) {
        leftBtn = left
        rightBtn = right
    }

    fun getLeftVisibility(): Boolean = (leftBtn != ACTION_NONE)

    fun getRightVisibility(): Boolean = (rightBtn != ACTION_NONE)

    fun getImage(direction: Int): Int {
        return when (direction) {
//            ACTION_BACK -> R.drawable.btn_back
            ACTION_BACK -> R.drawable.sel_btn_back
            ACTION_CLOSE -> R.drawable.sel_btn_close_b
//                if (darkMode) {
//                R.drawable.btn_close_b
//            } else {
//                R.drawable.btn_close_b
//            }
            ACTION_MENU -> R.drawable.sel_btn_close_b
            ACTION_NOTI -> if (mCount > 0) {
                R.drawable.sel_btn_close_b
            } else {
                R.drawable.sel_btn_close_b
            }
            ACTION_INFO, ACTION_NONE -> 0
            else -> 0
        }
    }

    fun getLeftImage(): Int {
        return getImage(leftBtn)
    }

    fun getRightImage(): Int {
        return getImage(rightBtn)
    }

    fun isDarkMode(): Boolean {
        return darkMode
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getBackgroundColor(): Int {
        return if (darkMode) {
            ApplicationProxy.getContext().getColor(R.color.gray_41444e)
        } else {
            ApplicationProxy.getContext().getColor(R.color.white)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getTitleButtonBg(): Int {
        return if (leftBtn == ACTION_MENU) {
            ApplicationProxy.getContext().getColor(R.color.blue_3e70cd)
        } else {
            ApplicationProxy.getContext().getColor(R.color.transparent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getTitleColor(): Int {
        return if (darkMode) {
            ApplicationProxy.getContext().getColor(R.color.white)
        } else {
            ApplicationProxy.getContext().getColor(R.color.black_141414)
        }
    }

    fun getTitleAlign() = mTitleAlign

//    fun getTitleAlign(): String {
//        return when (mTitleAlign) {
//            ALIGN_LEFT -> "viewStart"
//            ALIGN_CENTER -> "center"
//            ALIGN_RIGHT -> "viewEnd"
//            else -> "center"
//        }
//    }
}
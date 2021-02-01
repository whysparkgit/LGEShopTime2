package com.lge.lgshoptimem.ui.component

import com.lge.core.sys.Trace


class VideoPIPManager private constructor()
{
    companion object {
        private var mInstance: VideoPIPManager? = null
        private var mComponentList: ArrayList<ComponentListener> = ArrayList()
        private var mbPIPMode = false

        @JvmStatic
        fun getInstance(): VideoPIPManager = mInstance?:
            synchronized(this) {
                mInstance?: VideoPIPManager().also {
                    mInstance = it
                }
            }
    }

    interface ComponentListener {
        fun onPIPModeChanged(bPIPMode: Boolean)
    }

    fun addComponent(item: ComponentListener) {
        mComponentList.add(item)
        Trace.debug("-- addComponent() size = ${mComponentList.size}")
    }

    fun removeComponent(item: ComponentListener) {
        mComponentList.remove(item)
        Trace.debug("-- removeComponent() size = ${mComponentList.size}")
    }

    fun notifyModeChanged(bVal: Boolean) {
        Trace.debug("++ notifyValueChanged() bVal = $bVal size = ${mComponentList.size}")
        mbPIPMode = bVal

        mComponentList.forEach {
            it.onPIPModeChanged(bVal)
        }
    }

    fun isPIPMode() = mbPIPMode

    fun getCount() = mComponentList.size
}
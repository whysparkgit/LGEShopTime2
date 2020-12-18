package com.lge.shoptime.ui.common

interface AppConst {
    interface UPGRADE {
        companion object {
            const val UNKNOWN_APP_SOURCE = 100
            const val DOWNLOAD_COMPLETE = 200
            const val REQUEST_INSTALL = 300
            const val INSTALL_SUCCESS = 301
            const val INSTALL_FAILED = 302
            const val INSTALL_ABORTED = 303
            const val UNKNOWN_ERROR = 304
        }
    }
}
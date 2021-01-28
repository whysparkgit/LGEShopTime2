package com.lge.lgshoptimem.ui.common

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

    interface VIEWTYPE {
        companion object {
            const val VT_LIVE_CHANNEL_ICONS = 0
            const val VT_LIVE_CHANNELS = 1
            const val VT_POPULAR_SHOWS = 2
            const val VT_PRODUCT_IMAGE = 3
            const val VT_TODAY_DEAL = 4
            const val VT_HOT_PICKS = 5
            const val VT_NEXT_UPCOMING_HORIZONTAL = 6
            const val VT_NEXT_UPCOMING_VERTICAL = 7
            const val VT_YOU_MAY_LIKE = 8
            const val VT_CATEGORY_REMINDER = 9
            const val VT_KEYWORD_REMINDER = 10
            const val VT_MY_FAVORITES = 11
            const val VT_RECENTLY_VIEWED = 12
            const val VT_COUPON = 13
        }
    }
}
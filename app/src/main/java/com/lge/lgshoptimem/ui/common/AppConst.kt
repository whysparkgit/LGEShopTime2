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
            const val VT_TODAY_DEAL = 3
            const val VT_HOT_PICKS = 4
            const val VT_UPCOMING_HORIZONTAL = 5
            const val VT_UPCOMING_VERTICAL = 6
            const val VT_YOU_MAY_LIKE = 7
            const val VT_FAVORITE_CATEGORY = 8
            const val VT_FAVORITE_KEYWORD = 9
            const val VT_MY_FAVORITES = 10
            const val VT_RECENTLY_VIEWED = 11
            const val VT_COUPON = 12
            const val VT_PRODUCT_DETAIL = 13
            const val VT_PRODUCT_REVIEW = 14
            const val VT_SCHEDULE_DATE = 15
        }
    }

    interface KEY {
        companion object {
            const val PARTNER_ID = "PartnerId"
            const val PRODUCT_ID = "ProductId"
            const val CURATION_ID = "CurationId"
            const val LAUNCH_FROM = "LaunchFrom"
        }
    }

    interface VALUE {
        companion object {
            const val LIST = "List"
            const val PLAYER = "Player"

            const val INITIAL = 1
            const val CATEGORY = 2
            const val KEYWORD = 3
            const val MORE = 4
        }
    }
}
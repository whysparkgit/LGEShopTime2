package com.lge.core.sys

import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import com.lge.core.app.ApplicationProxy

class Const
{
    class NETWORK {
        class DEV_SERVER {
            companion object {
//                const val DOMAIN = "https://qt2-aic.lgshopsvc.lgappstv.com/"
//                const val PATH = "lgsp/v1/"
                const val DOMAIN = "https://qt2-aic-m.lgshopsvc.lgappstv.com/"
                const val PATH = "lgsp/m/v1/"
            }
        }

        class OPR_SERVER {
            companion object {
                const val DOMAIN = "http://qt2-aic.lgshopsvc.lgappstv.com/"
                const val PATH = "lgsp/v1/"
            }
        }
    }

    class STORAGE {
        companion object {
            @RequiresApi(Build.VERSION_CODES.N)
            var APP_ROOT = ApplicationProxy.getContext().dataDir.toString()

            var APP_MAIN = "$APP_ROOT/storage"

            var EXT_ROOT = ApplicationProxy.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()

            const val FILE_READ_BUFFER_SIZE = 1024 * 10
        }
    }
}
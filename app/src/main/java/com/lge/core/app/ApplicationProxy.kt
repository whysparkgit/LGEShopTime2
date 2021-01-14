package com.lge.core.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ComponentActivity
import com.lge.core.fs.StorageManager
import com.lge.core.net.NetworkManager
import com.lge.core.sys.Trace

class ApplicationProxy : Application()
{
    private var mbInitLaunch = false;
    private lateinit var mActivity: Activity
    private var mAppCompatActivity: AppCompatActivity? = null
    private var mLifeCycle: LifeCycle = LifeCycle()

    companion object {
        private lateinit var mInstance: ApplicationProxy

        @JvmStatic
        fun getInstance(): ApplicationProxy = mInstance

        @JvmStatic
        fun getContext(): Context = getInstance().applicationContext
    }

    fun getActivity(): AppCompatActivity? {
        if (mActivity is AppCompatActivity) {
            mAppCompatActivity = mActivity as AppCompatActivity
        }

        return mAppCompatActivity
    }

    override fun onCreate() {
        Trace.debug("++ Application onCreate()")
        super.onCreate()
        mInstance = this
        mbInitLaunch = true
        registerActivityLifecycleCallbacks(mLifeCycle)
        initialize()
    }

    override fun onTerminate() {
        Trace.debug("++ Application onTerminate()")
        super.onTerminate()
        unregisterActivityLifecycleCallbacks(mLifeCycle)
        finalize()
    }

    private fun initialize() {
        // TODO initialize
        StorageManager.getInstance().init()
        NetworkManager.getInstance().init()
//        DatabaseManager.getInstance().init()
//        DatabaseManager.getInstance().printStatus()
    }

    private fun finalize() {
        // TODO finalize
    }

    inner class LifeCycle : Application.ActivityLifecycleCallbacks
    {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            Trace.debug("++ onActivityCreated() : ${activity.localClassName}")
            mActivity = activity
        }

        override fun onActivityStarted(activity: Activity) {
            Trace.debug("++ onActivityStarted() : ${activity.localClassName}")

            if (mbInitLaunch) {
                mbInitLaunch = false

                // TODO
//                if (activity.localClassName != SplashActivity::class.simpleName) {
//                    Trace.debug(">> initial activity is not ${SplashActivity::class.simpleName}");
//                    activity.finish()
//                    val intent = Intent(getContext(), LoginActivity::class.java)
//                    startActivity(intent)
//                }
            }
        }

        override fun onActivityResumed(activity: Activity) {
            Trace.debug("++ onActivityResumed() : ${activity.localClassName}")
            mActivity = activity
        }

        override fun onActivityPaused(activity: Activity) {
            Trace.debug("++ onActivityPaused() : ${activity.localClassName}")
        }

        override fun onActivityStopped(activity: Activity) {
            Trace.debug("++ onActivityStopped() : ${activity.localClassName}")
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            Trace.debug("++ onActivitySaveInstanceState() : ${activity.localClassName}")
        }

        override fun onActivityDestroyed(activity: Activity) {
            Trace.debug("++ onActivityDestroyed() : ${activity.localClassName}")
        }
    }
}
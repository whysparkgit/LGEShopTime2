package com.lge.lgshoptimem.ui.home

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivityVideoPlayerBinding
import com.lge.lgshoptimem.ui.component.VideoPIPManager
import kotlinx.coroutines.*
import java.lang.Runnable

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class VideoPlayerActivity : AppCompatActivity(), SurfaceHolder.Callback
{
    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300

        private const val CONTROLLER_UPDATE_MS = 1000L

        private const val FADEOUT_SECONDS = 10  //3
    }

    private lateinit var mBinding: ActivityVideoPlayerBinding
    private val mMediaPlayer: MediaPlayer = MediaPlayer()
    private var mRunnable: Runnable? = null
    private var mHandler = Handler(Looper.getMainLooper())

    private val MediaPlayer.seconds: Int
        get() {
            return this.duration / 1000
        }

    private val MediaPlayer.currentSeconds: Int
        get() {
            return this.currentPosition / 1000
        }

    private val hideHandler = Handler()

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    private var mstrVideoUrl: String? = null
    private var mbCheckPlay: Boolean = false
    private var mRestartPosition: Int = 0
    private lateinit var mCheckPlayJob: Job
    private var mnFadeoutCount = -1
    private var mbNextUrl = false

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            MotionEvent.ACTION_UP -> view.performClick()
            else -> {
            }
        }
        false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_player)
        mBinding.listener = this

//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        isFullscreen = true

        mstrVideoUrl = intent.getStringExtra("param")
        Trace.debug(">> mstrVideoUrl = $mstrVideoUrl")

        mBinding.vvPlayer.holder.addCallback(this)

//        doMediaController(strUrl!!)
//        doMediaPlayer(mstrVideoUrl!!)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Trace.debug(">> surfaceCreated()")
        mMediaPlayer.setDisplay(mBinding.vvPlayer.holder)
        playByMediaPlayer()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Trace.debug(">> surfaceChanged()")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Trace.debug(">> surfaceDestroyed()")
    }

    private fun startPlayer() {
        mMediaPlayer.start()
        mbCheckPlay = true
        mnFadeoutCount = FADEOUT_SECONDS
        checkPlayable()
    }

    private fun pausePlayer() {
        mMediaPlayer.pause()
//        mRestartPosition = mMediaPlayer.currentPosition
        mbCheckPlay = false
    }

    private fun checkPlayable() {
        mCheckPlayJob = CoroutineScope(Dispatchers.Main).launch {
            while (mbCheckPlay) {
//                if (mMediaPlayer.isPlaying) {
//                    Trace.debug(">> checkPlayable() isPlaying = true")
//                    toggleIcon(mBinding.ivPause, mBinding.ivPlay)
//                    mnFadeoutCount = FADEOUT_SECONDS
//                    mMediaPlayer.pause()
//                } else {
//                    Trace.debug(">> checkPlayable() isPlaying = false")
//                    toggleIcon(mBinding.ivPlay, mBinding.ivPause)
//                    mnFadeoutCount = FADEOUT_SECONDS
//                    mMediaPlayer.start()
//                    updateSeekBar()
//                }

                Trace.debug(">> checkPlayable() updateSeekBar()")
                updateSeekBar()
                delay(CONTROLLER_UPDATE_MS)
            }
        }
    }

    fun doMediaController(strUrl: String) {
        val mc = MediaController(this)

        mBinding.vvPlayer.apply {
            setMediaController(mc)
            setVideoPath(strUrl)
            requestFocus()

            setOnPreparedListener {
                Trace.debug(">> doMediaController() OnPrepared() mc.height = ${mc.height}")
                start()
            }

            setOnCompletionListener {
                Trace.debug(">> doMediaController() OnCompletion()")
                mBinding.ivPlay.visibility = View.VISIBLE
                mBinding.vvPlayer.setZOrderOnTop(false)
            }

            setOnErrorListener {
                mp, what, extra ->  Trace.debug(">> doMediaController() OnError() what = $what extra = $extra")
                false
            }
        }
    }

    fun playByMediaPlayer(strUrl: String = mstrVideoUrl!!) {
        Trace.debug("++ playByMediaPlayer()")

        mMediaPlayer.apply {
//            reset()
            setDataSource(strUrl)
            setDisplay(mBinding.vvPlayer.holder)
            prepareAsync()

            setOnPreparedListener {
                Trace.debug(">> playByMediaPlayer() OnPrepared() videoHeight = ${videoHeight}")
                Trace.debug(">> playByMediaPlayer() OnPrepared() videoWidth = ${videoWidth}")
//                Trace.debug(">> playByMediaPlayer() OnPrepared() root.width = ${mBinding.root.width}")

                if (videoHeight > 0) {
                    val layoutParam: ViewGroup.LayoutParams = mBinding.vvPlayer.layoutParams
                    val nHeight = mBinding.root.width * videoHeight / videoWidth

                    layoutParam.height = if (nHeight > resources.displayMetrics.heightPixels) {
                        resources.displayMetrics.heightPixels
                    } else {
                        nHeight
                    }

                    mBinding.vvPlayer.layoutParams = layoutParam
                }

                setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
                initSeekBar()
                seekTo(mRestartPosition)
                startPlayer()
                mRestartPosition = 0
            }

            setOnCompletionListener {
                Trace.debug(">> playByMediaPlayer() OnCompletion()")
                pausePlayer()

                if (mbNextUrl) {
                    setDataSource(mstrVideoUrl)
                    prepareAsync()
                    mbNextUrl = false
                } else {
                    toggleIcon(mBinding.ivPause, mBinding.ivPlay)
                    fadeControlView(true, null)
                    mBinding.vvPlayer.setZOrderOnTop(false)
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Trace.debug(">> onConfigurationChanged() newConfig.orientation = ${newConfig.orientation}")
        Trace.debug(">> onConfigurationChanged() videoWidth = ${mMediaPlayer.videoWidth}")
        Trace.debug(">> onConfigurationChanged() videoHeight = ${mMediaPlayer.videoHeight}")
        Trace.debug(">> onConfigurationChanged() root.width = ${mBinding.root.width}")
        Trace.debug(">> onConfigurationChanged() root.height = ${mBinding.root.height}")
        Trace.debug(">> onConfigurationChanged() disp.width = ${resources.displayMetrics.widthPixels}")
        Trace.debug(">> onConfigurationChanged() disp.height = ${resources.displayMetrics.heightPixels}")
        super.onConfigurationChanged(newConfig)

        if (mMediaPlayer.videoWidth > 0) {
            val layoutParam: ViewGroup.LayoutParams = mBinding.vvPlayer.layoutParams

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                layoutParam.height = resources.displayMetrics.heightPixels
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutParam.height = resources.displayMetrics.widthPixels * mMediaPlayer.videoHeight / mMediaPlayer.videoWidth
            }

            mBinding.vvPlayer.layoutParams = layoutParam
            mBinding.vvPlayer.holder.setSizeFromLayout()
            Trace.debug(">> onConfigurationChanged() new height = ${layoutParam.height}")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        Trace.debug("++ onNewIntent()")
        super.onNewIntent(intent)

        val strUrl = intent?.getStringExtra("param")
        Trace.debug(">> onNewIntent() strUrl = $strUrl")

        if (mstrVideoUrl != strUrl) {
            mstrVideoUrl = strUrl
            mbNextUrl = true
        }
    }

    override fun onResume() {
        Trace.debug("++ onResume()")
        super.onResume()

        if (::mCheckPlayJob.isInitialized) {
            startPlayer()
        }
    }

    override fun onPause() {
        Trace.debug("++ onPause()")
        super.onPause()
        pausePlayer()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (isInPictureInPictureMode) {
                Trace.debug(">> onPause() PictureInPictureMode")
            } else {
                Trace.debug(">> onPause() not PictureInPictureMode")
            }

            VideoPIPManager.getInstance().notifyModeChanged(isInPictureInPictureMode)
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        Trace.debug("++ onPictureInPictureModeChanged() mode = $isInPictureInPictureMode config = $newConfig")
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        VideoPIPManager.getInstance().notifyModeChanged(isInPictureInPictureMode)

        if (isInPictureInPictureMode) {

        } else {
            if (mbNextUrl) {
                mMediaPlayer.stop()
                mMediaPlayer.reset()
            }
        }

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            Trace.debug(">> onPictureInPictureModeChanged() this.PIPMode = ${this.isInPictureInPictureMode}")
//            Trace.debug(">> onPictureInPictureModeChanged() AppProxy.PIPMode = ${ApplicationProxy.getInstance().getActivity()?.isInPictureInPictureMode}")
//        }
//
//        Trace.debug(">> this.packageName : ${this.packageName}")
//        Trace.debug(">> this.localClassName : ${this.localClassName}")
//        Trace.debug(">> this.componentName : ${this.componentName}")
//
//        Trace.debug(">> AppProxy.getActivity.packageName : ${ApplicationProxy.getInstance().getActivity()?.packageName}")
//        Trace.debug(">> AppProxy.getActivity.localClassName : ${ApplicationProxy.getInstance().getActivity()?.localClassName}")
//        Trace.debug(">> AppProxy.getActivity.componentName : ${ApplicationProxy.getInstance().getActivity()?.componentName}")
    }

    fun onClick(v: View) {
        Trace.debug("++ onClick() v = ${v.id}")

        when (v.id) {
            R.id.iv_play -> {
                startPlayer()
                fadeControlView(false) { toggleIcon(mBinding.ivPlay, mBinding.ivPause) }
            }

            R.id.iv_pause -> {
                pausePlayer()
                fadeControlView(false) { toggleIcon(mBinding.ivPause, mBinding.ivPlay) }
            }

            R.id.iv_close -> {
                finish()
            }

            R.id.iv_half_screen -> {
                finish()
            }

            R.id.iv_pip_screen -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
                        enterPictureInPictureMode(PictureInPictureParams.Builder().build())
                    }
                }
            }

            R.id.iv_setting -> {

            }

            R.id.iv_menu_portrait -> {

            }

            R.id.cl_video_player_root -> {
                mnFadeoutCount = FADEOUT_SECONDS
                fadeControlView(true, null)
            }

            R.id.cl_control_view -> {
                fadeControlView(false, null)
            }
        }
    }

    private fun Int.getTimeString(): String {
        return String.format("%02d:%02d:%02d", this / 3600, (this / 60) % 60, this % 60)
    }

    private fun initSeekBar() {
        Trace.debug("++ initSeekBar totalTime = ${mMediaPlayer.seconds}")
        mBinding.sbProgress.max = mMediaPlayer.seconds
        mBinding.sbProgress.progress = 0
        mBinding.tvTotalTime.text = mMediaPlayer.seconds.getTimeString()

        mBinding.sbProgress.apply {
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                    Trace.debug("++ onProgressChanged()")
                    if (!fromUser) return

                    mMediaPlayer.seekTo(progress * 1000)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
        }

        mMediaPlayer.setOnBufferingUpdateListener {
            mp, percent -> mBinding.sbProgress.secondaryProgress = percent
        }
    }

    private fun updateSeekBar() {
//        Trace.debug("++ updateSeekBar currentSeconds = ${mMediaPlayer?.currentSeconds}")
        if (mMediaPlayer.currentSeconds < 0) return

        mBinding.tvElapsedTime.text = mMediaPlayer.currentSeconds.getTimeString()
        mBinding.sbProgress.progress = mMediaPlayer.currentSeconds

        if (mnFadeoutCount == 0) {
            mnFadeoutCount = -1
            fadeControlView(false, null)
        } else if (mnFadeoutCount > 0) {
            mnFadeoutCount--
        }
    }

    private fun fadeControlView(bShow: Boolean, fEndTrans: ((Unit) -> (Unit))?) {
        val transition: Transition = Fade()
        transition.duration = if (bShow) 300 else 500
        transition.addTarget(mBinding.clControlView)
        transition.addListener(object: Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition?) {
            }

            override fun onTransitionEnd(transition: Transition?) {
                fEndTrans?.invoke(Unit)
            }

            override fun onTransitionCancel(transition: Transition?) {
            }

            override fun onTransitionPause(transition: Transition?) {
            }

            override fun onTransitionResume(transition: Transition?) {
            }
        })

        TransitionManager.beginDelayedTransition(mBinding.clVideoPlayerRoot, transition)
        mBinding.clControlView?.visibility = if (bShow) View.VISIBLE else View.GONE
    }

    private fun toggleIcon(to: View, from: View) {
        to.visibility = View.GONE
        from.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onDestroy() {
        Trace.debug("++ onDestroy()")
        super.onDestroy()

        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.stop()
        }

        mMediaPlayer.release()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        isFullscreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        isFullscreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }
}
package com.lge.lgshoptimem.ui.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.Handler
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.*
import android.widget.MediaController
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.lge.core.app.ApplicationProxy
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.CompVideoViewBinding
import com.lge.lgshoptimem.ui.home.VideoPlayerActivity
import kotlinx.coroutines.*
import java.lang.Runnable

class VideoViewComponent @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0):
        ConstraintLayout(context, attrs, defStyleAttr), SurfaceHolder.Callback
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

        private const val CHECK_PLAYABLE_MS = 300L

        private const val CONTROLLER_UPDATE_MS = 1000L

        private const val FADEOUT_SECONDS = 3

        @JvmStatic
        private var mTotalVideoView: Int = 0
    }

    private val mBinding: CompVideoViewBinding
    private var mMediaPlayer: MediaPlayer? = null
    /** Component Subject */
    var mstrSubject: String? = null

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
//        supportActionBar?.show()
    }
    private var isFullscreen: Boolean = false
    private val hideRunnable = Runnable { hide() }

    private var mstrVideoUrl: String? = null
    private var mstrThumbnailUrl: String? = null
    private var mbCheckPlay: Boolean = false
    private var mlDelayMillis = CHECK_PLAYABLE_MS
    private var mRestartPosition: Int = 0
    private lateinit var mCheckPlayJob: Job
    private var mnFadeoutCount = -1

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

            else -> {}
        }

        false
    }

    init {
        Trace.debug(">> init()")

        context.theme.obtainStyledAttributes(attrs, R.styleable.ListComponentAttrs, 0, 0).apply {
            mstrSubject = getString(R.styleable.ListComponentAttrs_subject)
            mstrVideoUrl = getString(R.styleable.ListComponentAttrs_videoUrl)
            mstrThumbnailUrl = getString(R.styleable.ListComponentAttrs_videoUrl)
            Trace.debug(">> video strUrl = $mstrVideoUrl")
            recycle()
        }

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.comp_video_view, this, true)
        mBinding.component = this
        mBinding.compVvPlayer.holder.addCallback(this)
        mBinding.compTvSubject.visibility = if (mstrSubject.isNullOrEmpty()) View.GONE else View.VISIBLE

//        playByMediaController(mStrUrl!!)
//        playByMediaPlayer(mStrUrl!!)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
//        Trace.debug("++ onLayout() l=$l t=$t r=$r b=$b root = ${mBinding.root}")
        mBinding.root.layout(l, t, r, b)
    }

    fun setSubject(strSubject: String) {
        Trace.debug("++ setSubject()")
        mstrSubject = strSubject
        findViewById<TextView>(R.id.comp_tv_subject)?.visibility = if (mstrSubject.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    fun getSubject() = mstrSubject

    fun setVideoUrl(strUrl: String?) {
        Trace.debug("++ setVideoUrl() strUrl = $strUrl")
        mstrVideoUrl = strUrl
//        mBinding.compVvPlayer.visibility = View.VISIBLE

//        if (!mstrVideoUrl.isNullOrEmpty() and (mMediaPlayer == null)) {
//            playByMediaPlayer(mstrVideoUrl!!)
//            mbCheckPlay = true
//            mlDelayMillis = CHECK_PLAYABLE_MS
//            checkPlayable()
//            mTotalVideoView++
//        }
    }

    fun getVideoUrl() = mstrVideoUrl

    fun setThumbnailUrl(strUrl: String?) {
        Trace.debug("++ setThumbnailUrl() strUrl = $strUrl")

        if (strUrl.isNullOrEmpty()) return
        // fixme
        mstrThumbnailUrl = strUrl.replace("https", "http", true)
        mstrThumbnailUrl = "http://cf-images.us-east-1.prod.boltdns.net/v1/static/6091058944001/af6c8d0d-1bc6-47ce-976f-a35fff09f131/8d49c6e7-b034-4018-93af-d59e95a4d8aa/1280x720/match/image.jpg"

        CommonBindingAdapter.setImageUrl(mBinding.compIvThumbnail, mstrThumbnailUrl)
        mbCheckPlay = true
        mlDelayMillis = CHECK_PLAYABLE_MS
        checkPlayable()
    }

    fun getThumbnailUrl() = mstrThumbnailUrl

    override fun onAttachedToWindow() {
        Trace.debug("++ onAttachedToWindow() this = $this")
        Trace.debug("++ onAttachedToWindow() mstrThumbnailUrl = $mstrThumbnailUrl")
        Trace.debug("++ onAttachedToWindow() mstrVideoUrl = $mstrVideoUrl")
        super.onAttachedToWindow()

        if (mstrVideoUrl.isNullOrEmpty() or mstrThumbnailUrl.isNullOrEmpty()) return

        if (!mCheckPlayJob.isActive) {
            mlDelayMillis = CHECK_PLAYABLE_MS
            mbCheckPlay = true
            checkPlayable()
        }
    }

    override fun onDetachedFromWindow() {
        Trace.debug("++ onDetachedFromWindow() this = $this")
        super.onDetachedFromWindow()

        if (mstrVideoUrl.isNullOrEmpty() or mstrThumbnailUrl.isNullOrEmpty()) return

        mbCheckPlay = false

        if (mMediaPlayer != null) {
            if (mMediaPlayer!!.isPlaying) {
                Trace.debug(">> mMediaPlayer stop and release")
                mMediaPlayer!!.stop()
            }
        }

        mMediaPlayer?.release()
        mMediaPlayer = null
        mTotalVideoView--
        Trace.debug("-- onDetachedFromWindow() mTotalVideoView = $mTotalVideoView")
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        Trace.debug(">> onWindowFocusChanged() hasWindowFocus = $hasWindowFocus")
        super.onWindowFocusChanged(hasWindowFocus)

        if (mMediaPlayer != null) {
            if (hasWindowFocus) {
                Trace.debug(">> onWindowFocusChanged() mJob.isActive = ${mCheckPlayJob.isActive}")

                if (!mCheckPlayJob.isActive) {
                    mbCheckPlay = true
                    checkPlayable()
                }
            } else {
                mMediaPlayer?.pause()
                mRestartPosition = mMediaPlayer!!.currentPosition
                mbCheckPlay = false
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Trace.debug(">> init() surfaceCreated()")
        mMediaPlayer?.setDisplay(mBinding.compVvPlayer.holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Trace.debug(">> init() surfaceChanged()")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Trace.debug(">> init() surfaceDestroyed()")
    }

    private fun checkPlayable() {
        mTotalVideoView++
        Trace.debug("++ checkPlayable() mTotalVideoView = $mTotalVideoView")

        mCheckPlayJob = CoroutineScope(Dispatchers.Main).launch {
            while (mbCheckPlay) {
                if (isPlayable()) {
                    mBinding.compIvThumbnail.visibility = View.GONE

                    if (mMediaPlayer == null) {
                        Trace.debug(">> checkPlayable() isPlayable = true MediaPlayer")
                        toggleIcon(mBinding.ivPlay, mBinding.ivPause)
                        playByMediaPlayer()
                    } else if (!mMediaPlayer!!.isPlaying) {
                        Trace.debug(">> checkPlayable() isPlayable = true start")
                        toggleIcon(mBinding.ivPlay, mBinding.ivPause)
                        mnFadeoutCount = FADEOUT_SECONDS
                        mMediaPlayer?.start()
                    } else {
                        Trace.debug(">> checkPlayable() isPlayable = true else")
                        mlDelayMillis = CONTROLLER_UPDATE_MS
                    }
                } else {
                    Trace.debug(">> checkPlayable() isPlayable = false mMediaPlayer = $mMediaPlayer")
                    mBinding.compIvThumbnail.visibility = View.VISIBLE

                    if (mMediaPlayer != null) {
                        if (mMediaPlayer!!.isPlaying) {
                            Trace.debug(">> checkPlayable() isPlaying = false")
                            toggleIcon(mBinding.ivPause, mBinding.ivPlay)
                            mMediaPlayer?.pause()
                            mlDelayMillis = CHECK_PLAYABLE_MS
                        }
                    }
                }

                if (mlDelayMillis == CONTROLLER_UPDATE_MS) {
//                    Trace.debug(">> checkPlayable() updateSeekBar()")
                    updateSeekBar()
                }

//                Trace.debug(">> mTotalVideoView = $mTotalVideoView")
                delay(mlDelayMillis)
            }
        }
    }

    private fun isPlayable(): Boolean {
        val videoRect = Rect()
        mBinding.compVvPlayer.getGlobalVisibleRect(videoRect)
        val margin: Int = (Resources.getSystem().displayMetrics.heightPixels * 0.5).toInt()
        val borderRect = Rect(0, margin, Resources.getSystem().displayMetrics.widthPixels,
                Resources.getSystem().displayMetrics.heightPixels - margin)

        return borderRect.intersect(videoRect)
    }

    fun playByMediaController(strUrl: String) {
        val mc = MediaController(context)

        mBinding.compVvPlayer.apply {
            setMediaController(mc)
            setVideoPath(strUrl)
            requestFocus()

            setOnPreparedListener {
                Trace.debug(">> doMediaController() OnPrepared() mc.height = ${mc.height}")
                start()
            }

            setOnCompletionListener {
                Trace.debug(">> doMediaController() OnCompletion()")
//                mBinding.vDim.visibility = View.VISIBLE
                mBinding.ivPlay.visibility = View.VISIBLE
                mBinding.compVvPlayer.setZOrderOnTop(false)
            }

            setOnErrorListener {
                mp, what, extra ->  Trace.debug(">> doMediaController() OnError() what = $what extra = $extra")
                false
            }
        }
    }

    fun playByMediaPlayer(strUrl: String = mstrVideoUrl!!) {
        mMediaPlayer = MediaPlayer()

//        mBinding.compVvPlayer.holder.addCallback(object: SurfaceHolder.Callback {
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                Trace.debug(">> doMediaPlayer() surfaceCreated() this = $this")

//                CoroutineScope(Dispatchers.Main).launch {
                    mMediaPlayer?.apply {
                        Trace.debug(">> mMediaPlayer is not null")
                        reset()
                        setDataSource(strUrl)
                        setDisplay(mBinding.compVvPlayer.holder)
                        prepareAsync()

                        setOnPreparedListener {
                            try {
                                Trace.debug(">> doMediaPlayer() OnPrepared() it.videoWidth = ${it.videoWidth}")
                                Trace.debug(">> doMediaPlayer() OnPrepared() it.videoHeight = ${it.videoHeight}")
//                        Trace.debug(">> doMediaPlayer() OnPrepared() videoWidth = ${videoWidth}")
//                        Trace.debug(">> doMediaPlayer() OnPrepared() videoHeight = ${videoHeight}")
//                        Trace.debug(">> doMediaPlayer() OnPrepared() root.width = ${mBinding.root.width}")


                                CoroutineScope(Dispatchers.Main).launch {

//                                    if (it.videoHeight > 0) {
//                                        val layoutParam: ViewGroup.LayoutParams = mBinding.vvPlayer.layoutParams
//                                        val nHeight = if (videoWidth > 0) mBinding.root.width * videoHeight / videoWidth else 0
//                                        Trace.debug(">> doMediaPlayer() OnPrepared() layout.height = $nHeight")
//
//                                        layoutParam.height = if (nHeight > resources.displayMetrics.heightPixels) {
//                                            resources.displayMetrics.heightPixels
//                                        } else {
//                                            nHeight
//                                        }
//
//                                        Trace.debug(">> doMediaPlayer() OnPrepared() layoutParam.height = ${layoutParam.height}")
//                                        mBinding.vvPlayer.layoutParams = layoutParam
//                                    }

                                    setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
                                    initSeekBar()
                                    seekTo(mRestartPosition)
                                    setVolume(0f, 0f)
                                    start()
                                    mnFadeoutCount = FADEOUT_SECONDS
                                    mRestartPosition = 0
                                }
                            } catch (e: IllegalStateException) {
                                e.printStackTrace()
                            }
                        }

                        setOnCompletionListener {
                            Trace.debug(">> doMediaPlayer() OnCompletion()")
//                            mBinding.compClControlView.visibility = View.VISIBLE
                            mBinding.compVvPlayer.setZOrderOnTop(false)
                        }
                    }
//                }
//            }
//
//            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
//                Trace.debug(">> doMediaPlayer() surfaceChanged() width = ${width}")
//                Trace.debug(">> doMediaPlayer() surfaceChanged() height = ${height}")
////                Trace.debug(">> doMediaPlayer() surfaceChanged() videoWidth = ${mMediaPlayer?.videoWidth}")
////                Trace.debug(">> doMediaPlayer() surfaceChanged() videoHeight = ${mMediaPlayer?.videoHeight}")
////                Trace.debug(">> doMediaPlayer() surfaceChanged() root.width = ${mBinding.root.width}")
////                Trace.debug(">> doMediaPlayer() surfaceChanged() root.height = ${mBinding.root.height}")
////                Trace.debug(">> doMediaPlayer() surfaceChanged() disp.width = ${resources.displayMetrics.widthPixels}")
////                Trace.debug(">> doMediaPlayer() surfaceChanged() disp.height = ${resources.displayMetrics.heightPixels}")
//            }
//
//            override fun surfaceDestroyed(holder: SurfaceHolder) {
//                Trace.debug(">> doMediaPlayer() surfaceDestroyed() this = $this")
//            }
//        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Trace.debug(">> onConfigurationChanged() newConfig.orientation = ${newConfig.orientation}")
        Trace.debug(">> onConfigurationChanged() videoWidth = ${mMediaPlayer?.videoWidth}")
        Trace.debug(">> onConfigurationChanged() videoHeight = ${mMediaPlayer?.videoHeight}")
//        Trace.debug(">> onConfigurationChanged() root.width = ${mBinding.root.width}")
//        Trace.debug(">> onConfigurationChanged() root.height = ${mBinding.root.height}")
//        Trace.debug(">> onConfigurationChanged() disp.width = ${resources.displayMetrics.widthPixels}")
//        Trace.debug(">> onConfigurationChanged() disp.height = ${resources.displayMetrics.heightPixels}")

        super.onConfigurationChanged(newConfig)

        val layoutParam: ViewGroup.LayoutParams = mBinding.compVvPlayer.layoutParams

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutParam.height = resources.displayMetrics.heightPixels
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutParam.height = resources.displayMetrics.widthPixels * mMediaPlayer!!.videoHeight / mMediaPlayer!!.videoWidth
        }

        mBinding.compVvPlayer.layoutParams = layoutParam
        mBinding.compVvPlayer.holder.setSizeFromLayout()
        Trace.debug(">> onConfigurationChanged() new height = ${layoutParam.height}")
    }

    fun onClick(v: View) {
        Trace.debug("++ onClick() v = ${v.id}")

        when (v.id) {
            R.id.iv_play -> {
                mbCheckPlay = true
                checkPlayable()

                var fEndTrans: ((Unit) -> (Unit))? = {
                    toggleIcon(mBinding.ivPlay, mBinding.ivPause)
                }

                fadeControlView(false, fEndTrans)
            }

            R.id.iv_pause -> {
                mMediaPlayer?.pause()
                mbCheckPlay = false

                var fEndTrans: ((Unit) -> (Unit))? = {
                    toggleIcon(mBinding.ivPause, mBinding.ivPlay)
                }

                fadeControlView(false, fEndTrans)
            }

            R.id.iv_mute -> {
                toggleIcon(mBinding.ivMute, mBinding.ivSpeaker)
                mMediaPlayer?.setVolume(1f, 1f)
            }

            R.id.iv_speaker -> {
                toggleIcon(mBinding.ivSpeaker, mBinding.ivMute)
                mMediaPlayer?.setVolume(0f, 0f)
            }

            R.id.iv_full_screen -> {
                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra("param", mstrVideoUrl)
                ApplicationProxy.getInstance().getActivity()?.startActivity(intent)
            }

            R.id.comp_vv_player -> {
                mnFadeoutCount = FADEOUT_SECONDS
                fadeControlView(true, null)
            }

            R.id.comp_iv_thumbnail -> {
                mnFadeoutCount = FADEOUT_SECONDS
                fadeControlView(true, null)
            }

            R.id.comp_cl_control_view -> {
                fadeControlView(false, null)
            }
        }
    }

    private fun getTimeString(nSec: Int): String {
        return String.format("%02d:%02d:%02d", nSec / 3600, (nSec / 60) % 60, nSec % 60)
    }

    private fun initSeekBar() {
        Trace.debug("++ initSeekBar totalTime = ${mMediaPlayer?.seconds}")
        mBinding.sbProgress.max = mMediaPlayer!!.seconds
        mBinding.sbProgress.progress = 0
//        mBinding.sbProgress.secondaryProgress = 0

        mBinding.sbProgress.apply {
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                    Trace.debug("++ onProgressChanged()")
                    if (!fromUser) return

                    mMediaPlayer?.seekTo(progress * 1000)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
        }
    }

    private fun updateSeekBar() {
//        Trace.debug("++ updateSeekBar currentSeconds = ${mMediaPlayer?.currentSeconds}")

        if (mMediaPlayer!!.currentSeconds < 0) return

        mBinding.tvElapsedTime.text = getTimeString(mMediaPlayer!!.currentSeconds)
        mBinding.sbProgress.progress = mMediaPlayer!!.currentSeconds

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
        transition.addTarget(mBinding.compClControlView)
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

        TransitionManager.beginDelayedTransition(mBinding.compClVideoView, transition)
        mBinding.compClControlView.visibility = if (bShow) View.VISIBLE else View.GONE
    }

    private fun toggleIcon(to: View, from: View) {
        to.visibility = View.GONE
        from.visibility = View.VISIBLE
    }

//    override fun onPostCreate(savedInstanceState: Bundle?) {
//        super.onPostCreate(savedInstanceState)
//
//        // Trigger the initial hide() shortly after the activity has been
//        // created, to briefly hint to the user that UI controls
//        // are available.
//        delayedHide(100)
//    }

    private fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
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
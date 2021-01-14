package com.lge.lgshoptimem.ui.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.*
import android.widget.MediaController
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.CompVideoViewBinding

class VideoViewComponent @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0):
        ConstraintLayout(context, attrs, defStyleAttr)
{
    private val mBinding: CompVideoViewBinding
    private lateinit var mMediaPlayer: MediaPlayer
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
//        supportActionBar?.show()
    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    var mStrUrl: String? = ""

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

    init {
        Trace.debug(">> init()")
//        var strUrl: String? = ""

        context.theme.obtainStyledAttributes(attrs, R.styleable.ListComponentAttrs, 0, 0).apply {
            mStrUrl = getString(R.styleable.ListComponentAttrs_videoUrl)
            Trace.debug(">> video strUrl = $mStrUrl")
            recycle()
        }

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.comp_video_view, this, true)
        mBinding.component = this

//        doMediaController(strUrl!!)
//        doMediaPlayer(strUrl!!)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Trace.debug("++ onLayout() l=$l t=$t r=$r b=$b root = ${mBinding.root}")
        mBinding.root.layout(l, t, r, b)
    }

    fun doMediaController(strUrl: String) {
        val mc = MediaController(context)

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
                mBinding.vDim.visibility = View.VISIBLE
                mBinding.ivPlay.visibility = View.VISIBLE
                mBinding.vvPlayer.setZOrderOnTop(false)
            }

            setOnErrorListener {
                mp, what, extra ->  Trace.debug(">> doMediaController() OnError() what = $what extra = $extra")
                false
            }
        }
    }

    fun doMediaPlayer(strUrl: String) {
        mMediaPlayer = MediaPlayer()

        mBinding.vvPlayer.holder.addCallback(object: SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                Trace.debug(">> doMediaPlayer() surfaceCreated()")

                mMediaPlayer.apply {
                    setDataSource(strUrl)
                    setDisplay(mBinding.vvPlayer.holder)
                    prepareAsync()

                    setOnPreparedListener {
                        Trace.debug(">> doMediaPlayer() OnPrepared() videoHeight = ${videoHeight}")
                        Trace.debug(">> doMediaPlayer() OnPrepared() videoWidth = ${videoWidth}")
                        Trace.debug(">> doMediaPlayer() OnPrepared() root.width = ${mBinding.root.width}")

                        val layoutParam: ViewGroup.LayoutParams = mBinding.vvPlayer.layoutParams
                        var nHeight = if (videoWidth > 0) mBinding.root.width * videoHeight / videoWidth else 0
                        Trace.debug(">> doMediaPlayer() OnPrepared() layout.height = $nHeight")

                        layoutParam.height = if (nHeight > resources.displayMetrics.heightPixels) {
                            resources.displayMetrics.heightPixels
                        } else {
                            nHeight
                        }

                        mBinding.vvPlayer.layoutParams = layoutParam
                        setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
                        initSeekBar()
                        updateSeekBar()
                        start()
                    }

                    setOnCompletionListener {
                        Trace.debug(">> doMediaPlayer() OnCompletion()")
                        mBinding.vDim.visibility = View.VISIBLE
                        mBinding.ivPlay.visibility = View.VISIBLE
                        mBinding.vvPlayer.setZOrderOnTop(false)
                    }
                }
            }

            override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
            ) {
                Trace.debug(">> doMediaPlayer() surfaceChanged() width = ${width}")
                Trace.debug(">> doMediaPlayer() surfaceChanged() height = ${height}")
                Trace.debug(">> doMediaPlayer() surfaceChanged() videoWidth = ${mMediaPlayer.videoWidth}")
                Trace.debug(">> doMediaPlayer() surfaceChanged() videoHeight = ${mMediaPlayer.videoHeight}")
                Trace.debug(">> doMediaPlayer() surfaceChanged() root.width = ${mBinding.root.width}")
                Trace.debug(">> doMediaPlayer() surfaceChanged() root.height = ${mBinding.root.height}")
                Trace.debug(">> doMediaPlayer() surfaceChanged() disp.width = ${resources.displayMetrics.widthPixels}")
                Trace.debug(">> doMediaPlayer() surfaceChanged() disp.height = ${resources.displayMetrics.heightPixels}")
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Trace.debug(">> doMediaPlayer() surfaceDestroyed()")
                mRunnable?.run { mHandler.removeCallbacks(mRunnable!!) }
                mMediaPlayer.release()
            }
        })
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

    fun onClick(v: View) {
        when (v.id) {
            R.id.iv_play -> {
                doMediaPlayer(mStrUrl!!)
//                if (!mMediaPlayer.isPlaying) {
//                    mBinding.vDim.visibility = View.GONE
//                    mBinding.ivPlay.visibility = View.GONE
//                    mMediaPlayer.start()
//                }
            }

            R.id.iv_share -> {}
            R.id.iv_half_screen -> {}
            R.id.iv_pip_screen -> {}
            R.id.iv_setting -> {}
            R.id.iv_menu -> {}
        }
    }

    private fun getTimeString(nSec: Int): String {
        return String.format("%02d:%02d:%02d", nSec / 3600, (nSec / 60) % 60, nSec % 60)
    }

    private fun initSeekBar() {
        Trace.debug("++ initSeekBar totalTime = ${mMediaPlayer.seconds}")
        mBinding.sbProgress.max = mMediaPlayer.seconds
        mBinding.sbProgress.progress = 0
//        mBinding.sbProgress.secondaryProgress = 0

        mBinding.sbProgress.apply {
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    Trace.debug("++ onProgressChanged()")
                    if (!fromUser) return

                    mMediaPlayer.seekTo(progress * 1000)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
        }
    }

    private fun updateSeekBar() {
        mRunnable = Runnable {
            mBinding.tvElapsedTime.text = getTimeString(mMediaPlayer.currentSeconds)
            mBinding.sbProgress.progress = mMediaPlayer.currentSeconds
            mHandler.postDelayed(mRunnable!!, 1000)
        }

        mHandler.postDelayed(mRunnable!!, 1000)
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
    }

}
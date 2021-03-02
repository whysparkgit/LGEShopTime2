package com.lge.lgshoptimem.ui.component

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.media.MediaPlayer
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.*
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.lge.core.app.ApplicationProxy
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.CompVideoViewBinding
import com.lge.lgshoptimem.model.dto.Video
import com.lge.lgshoptimem.ui.product.ExoPlayerActivity
import kotlinx.coroutines.*

class VideoViewComponent @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0):
        ConstraintLayout(context, attrs, defStyleAttr), SurfaceHolder.Callback, VideoPIPManager.ComponentListener
{
    companion object {
        private const val CHECK_PLAYABLE_MS = 300L
        private const val CONTROLLER_UPDATE_MS = 1000L
        private const val FADEOUT_SECONDS = 3
    }

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

    private val mBinding: CompVideoViewBinding
    private var mMediaPlayer: MediaPlayer? = null
    private var mstrVideoUrl: String? = null
    private var mstrThumbnailUrl: String? = null
    private var mViewdata: Video? = null
    private var mbCheckPlay: Boolean = false
    private var mlDelayMillis = CHECK_PLAYABLE_MS
    private var mRestartPosition: Int = 0
    private lateinit var mCheckPlayJob: Job
    private var mnFadeoutCount = -1
    private var mHasWindowFocus = false
    private var mVolume = 0

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
    }

    fun getVideoUrl() = mstrVideoUrl

    fun setThumbnailUrl(strUrl: String?) {
        Trace.debug("++ setThumbnailUrl() strUrl = $strUrl")

        if (mstrVideoUrl.isNullOrEmpty() or strUrl.isNullOrEmpty()) return

        mstrThumbnailUrl = strUrl
        CommonBindingAdapter.setImageUrl(mBinding.compIvThumbnail, mstrThumbnailUrl)

        if (::mCheckPlayJob.isInitialized) {
            if (mCheckPlayJob.isActive) {
                mbCheckPlay = false
                if (mCheckPlayJob.isActive) mCheckPlayJob.cancel()
                stop()
            }
        }

        start()
    }

    fun getThumbnailUrl() = mstrThumbnailUrl

    fun setViewdata(viewdata: Video?) {
        if (viewdata == null) return

        Trace.debug("++ setViewdata() fromDetail = ${viewdata.fromDetail}")
        mViewdata = viewdata
        mBinding.viewdata = mViewdata
//        CommonBindingAdapter.setImageUrl(mBinding.compIvLogo, viewdata.patncLogoPath)
    }

    fun getViewdata() = mViewdata

    override fun onAttachedToWindow() {
        Trace.debug("++ onAttachedToWindow() this = $this")
        Trace.debug("++ onAttachedToWindow() mstrThumbnailUrl = $mstrThumbnailUrl")
        Trace.debug("++ onAttachedToWindow() mstrVideoUrl = $mstrVideoUrl")
        super.onAttachedToWindow()
        VideoPIPManager.getInstance().addComponent(this)

        if (mstrVideoUrl.isNullOrEmpty() or mstrThumbnailUrl.isNullOrEmpty()) return

        if (::mCheckPlayJob.isInitialized and mCheckPlayJob.isActive) return

        start()
    }

    override fun onDetachedFromWindow() {
        Trace.debug("++ onDetachedFromWindow() this = $this")
        super.onDetachedFromWindow()
        VideoPIPManager.getInstance().removeComponent(this)

        if (mstrVideoUrl.isNullOrEmpty() or mstrThumbnailUrl.isNullOrEmpty()) return

        mbCheckPlay = false
        if (mCheckPlayJob.isActive) mCheckPlayJob.cancel()
        stop()
    }

    override fun onPIPModeChanged(bPIPMode: Boolean) {
        Trace.debug(">> onPIPModeChanged() bPIPMode = $bPIPMode")

        if (bPIPMode) {
            pause()
        } else {
            start()
        }
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        Trace.debug(">> onWindowFocusChanged() hasWindowFocus = $hasWindowFocus")
        super.onWindowFocusChanged(hasWindowFocus)
        mHasWindowFocus = hasWindowFocus

        if (mMediaPlayer == null) return

        if (hasWindowFocus and !VideoPIPManager.getInstance().isPIPMode()) {
            Trace.debug(">> onWindowFocusChanged() mJob.isActive = ${mCheckPlayJob.isActive}")

            if (!mCheckPlayJob.isActive) {
                stop()
                start()
            }
        } else {
            pause()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Trace.debug(">> surfaceCreated()")
        mMediaPlayer?.setDisplay(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Trace.debug(">> surfaceChanged()")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Trace.debug(">> surfaceDestroyed()")
    }

    private fun start() {
        mlDelayMillis = CHECK_PLAYABLE_MS
        mbCheckPlay = true
        checkPlayable()
    }

    private fun pause() {
        mbCheckPlay = false
        mMediaPlayer?.pause()
        toggleIcon(mBinding.ivPause, mBinding.ivPlay)
        updateSeekBar()
        mRestartPosition = mMediaPlayer!!.currentPosition
    }

    private fun stop() {
        Trace.debug("++ mMediaPlayer stop and release")
        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        mMediaPlayer = null
    }

    private fun checkPlayable() {
        mCheckPlayJob = CoroutineScope(Dispatchers.Main).launch {
            while (mbCheckPlay) {
                if (measurePlayable() and !VideoPIPManager.getInstance().isPIPMode()) {
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

    private fun measurePlayable(): Boolean {
        val videoRect = Rect()
        mBinding.compVvPlayer.getGlobalVisibleRect(videoRect)
        val margin: Int = (Resources.getSystem().displayMetrics.heightPixels * 0.4).toInt()
        val borderRect = Rect(0, margin / 2, Resources.getSystem().displayMetrics.widthPixels,
                Resources.getSystem().displayMetrics.heightPixels - margin)

        return borderRect.intersect(videoRect)
    }

    fun playByMediaPlayer(strUrl: String = mstrVideoUrl!!) {
        mMediaPlayer = MediaPlayer()

        mMediaPlayer?.apply {
            Trace.debug(">> playByMediaPlayer()")
            reset()
            setDataSource(strUrl)
            setDisplay(mBinding.compVvPlayer.holder)
            prepareAsync()

            setOnPreparedListener {
                try {
                    Trace.debug(">> OnPrepared() it.videoWidth = ${it.videoWidth} it.videoHeight = ${it.videoHeight}")

                    CoroutineScope(Dispatchers.Main).launch {
                        setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
                        initSeekBar()
//                        if (mRestartPosition != 0) seekTo(mRestartPosition)
                        setVolume(mVolume)
                        start()
                        mnFadeoutCount = FADEOUT_SECONDS
                        mRestartPosition = 0
                    }
                } catch (e1: IllegalStateException) {
                    e1.printStackTrace()
                } catch (e2: RuntimeException) {
                    e2.printStackTrace()
                }
            }

            setOnCompletionListener {
                Trace.debug(">> OnCompletion()")
//                mBinding.compClControlView.visibility = View.VISIBLE
//                toggleIcon(mBinding.ivPause, mBinding.ivPlay)
                mBinding.compVvPlayer.setZOrderOnTop(false)
            }
        }
    }

    fun onClick(v: View) {
        Trace.debug("++ onClick() v = ${v.id}")

        when (v.id) {
            R.id.iv_play -> {
                mbCheckPlay = true
                checkPlayable()
                fadeControlView(false) { toggleIcon(mBinding.ivPlay, mBinding.ivPause) }
            }

            R.id.iv_pause -> {
                mMediaPlayer?.pause()
                mbCheckPlay = false
                fadeControlView(false) { toggleIcon(mBinding.ivPause, mBinding.ivPlay) }
            }

            R.id.iv_mute -> {
                if (!mMediaPlayer!!.isPlaying) return
                setVolume(1)
            }

            R.id.iv_speaker -> {
                if (!mMediaPlayer!!.isPlaying) return
                setVolume(0)
            }

            R.id.iv_full_screen -> {
//                val intent = Intent(context, VideoPlayerActivity::class.java)
                val intent = Intent(context, ExoPlayerActivity::class.java)
                intent.putExtra("param", mBinding.viewdata)
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

    private fun Int.getTimeString(): String {
        return String.format("%02d:%02d:%02d", this / 3600, (this / 60) % 60, this % 60)
    }

    private fun initSeekBar() {
        Trace.debug("++ initSeekBar totalTime = ${mMediaPlayer?.seconds}")
        mBinding.sbProgress.max = mMediaPlayer!!.seconds
        mBinding.sbProgress.progress = 0

        if (mMediaPlayer!!.seconds > 0) {
            mBinding.tvTotalTime?.text = mMediaPlayer!!.seconds.getTimeString()
        }

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

        mMediaPlayer?.setOnBufferingUpdateListener {
            mp, percent -> mBinding.sbProgress.secondaryProgress = percent
        }
    }

    private fun updateSeekBar() {
//        Trace.debug("++ updateSeekBar currentSeconds = ${mMediaPlayer?.currentSeconds}")
        if (mMediaPlayer!!.currentSeconds < 0) return

        mBinding.tvElapsedTime.text = mMediaPlayer?.currentSeconds?.getTimeString()
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

    private fun toggleIcon(from: View, to: View) {
        from.visibility = View.GONE
        to.visibility = View.VISIBLE
    }

    private fun setVolume(nVol: Int) {
        when (nVol) {
            0 -> {
                toggleIcon(mBinding.ivSpeaker, mBinding.ivMute)
                mMediaPlayer?.setVolume(0f, 0f)
                mVolume = 0
            }

            1 -> {
                toggleIcon(mBinding.ivMute, mBinding.ivSpeaker)
                mMediaPlayer?.setVolume(1f, 1f)
                mVolume = 1
            }
        }
    }
}
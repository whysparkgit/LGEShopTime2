package com.lge.lgshoptimem.ui.product

import android.animation.ObjectAnimator
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.addListener
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableInt
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.SelectionOverride
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.util.Clock
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.video.VideoListener
import com.google.common.collect.Lists
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.R
import com.lge.lgshoptimem.databinding.ActivityExoPlayerBinding
import com.lge.lgshoptimem.model.dto.Video
import com.lge.lgshoptimem.model.dto.WatchNow
import com.lge.lgshoptimem.ui.component.ComponentItemListener
import com.lge.lgshoptimem.ui.component.VideoPIPManager
import kotlinx.coroutines.*

class ExoPlayerActivity : AppCompatActivity(), ComponentItemListener
{
    companion object {
        private const val CONTROLLER_UPDATE_MS = 1000L
        private const val FADEOUT_SECONDS = 3

        private const val SUBTITLE_TTML = ".ttml"
        private const val SUBTITLE_WEBVVT = ".webvtt"
        private const val SUBTITLE_SUBRIP = ".srt"

//        HLS
//        .m3u8	vnd.apple.mpegURL
//        .ts	video/MP2T
//        .mp4	video/mp4

//        DASH
//        .mp4	video/mp4
//        .m4v	video
//        .m4s	video/iso.segment
//        .m4a	audio/mp4

//        Smooth Streaming
//                application/vnd.ms-sstr+xml (MS HTTP-based adaptive streaming)
//        *.ismv  video/audio, video
//        *.isma  audio

//        Closed Caption
//         CC   application/cea-608
//        .webvtt text/vtt
//        .ttml   application/ttml+xml
//        .srt    application/x-subrip
    }

    private lateinit var mBinding: ActivityExoPlayerBinding
    private var mVideo: Video? = null
    private var mPlayer: SimpleExoPlayer? = null
    private val mViewModel: VideoPlayerViewModel by viewModels()

    private var mbCheckPlay: Boolean = false
    private var mRestartPosition: Long = 0L
    private lateinit var mCheckPlayJob: Job
    private var mnFadeoutCount = -1
    private var mnElapsedTime: Int = 0

    private var mnVideoTrackGroupIndex = -1
    private var mnTextTrackGroupIndex = -1
    private var mnQualitySDIndex: Int = -1      // 720 x 480
    private var mnQualityHDIndex: Int = -1      // 1280 x 720
    private var mnQualityFHDIndex: Int = -1     // 1920 x 1080
    private var mnCurrentQuality: Int = -1
    private var mbSubtitleEnabled: Boolean = false
    private var mbShowSettingView: Boolean = false
    private var mbShowChannelView: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Trace.debug("++ onCreate()")
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_exo_player)
        mBinding.listener = this

        mVideo = intent.getParcelableExtra<Video>("param")

        if (mVideo == null) return

        mBinding.viewdata = mVideo
        mBinding.qualitySD = mnQualitySDIndex
        mBinding.qualityHD = mnQualityHDIndex
        mBinding.qualityFHD = mnQualityFHDIndex

        if (!mVideo?.showSubtitlUrl.isNullOrEmpty()) {
            mBinding.swSettingCc.isEnabled = true
        }

        mViewModel.mldVideoProduct.observe(this, this::onDataListChanged)

//        mVideo?.showUrl = "https://vod-301f.kxcdn.com/OEMLGTV_LUMINESSSILKV111E1PIHD_115669_800-314-6659.mp4"
//        mVideo?.showSubtitlUrl = "https://tvoemstorage.blob.core.windows.net/lgtv/cc/OEMLGTV_LUMINESSSILKV111E1PIHD_115669_800-314-6659.srt"

//        mVideo?.showUrl = "https://cdn3.wowza.com/1/eUdsNEcyMmRvckor/K3pydHZw/hls/live/playlist.m3u8"
//        mVideo?.showSubtitlUrl = "https://tvoemstorage.blob.core.windows.net/lgtv/cc/OEMLGTV_LUMINESSSILKV111E1PIHD_115669_800-314-6659.srt"

        Trace.debug(">> Video Url = ${mVideo?.showUrl}")
        Trace.debug(">> Subtitle Url = ${mVideo?.showSubtitlUrl}")
        Trace.debug(">> fromDetail = ${mVideo?.fromDetail}")

        if (savedInstanceState != null) {
            Trace.debug("onSaveInstanceState not null")

            mRestartPosition = savedInstanceState.getLong("CURRENT_POSITION", 0L)
            mBinding.vwSettingDim.visibility = savedInstanceState.getInt("SETTING_DIM_VISIBLE", View.GONE)
            mnCurrentQuality = savedInstanceState.getInt("CURRENT_QUALITY", -1)
            mbSubtitleEnabled = savedInstanceState.getBoolean("SHOW_SUBTITLE", false)
            mbShowSettingView = savedInstanceState.getBoolean("SHOW_SETTING", false)
            mbShowChannelView = savedInstanceState.getBoolean("SHOW_CHANNEL", false)

            Trace.debug(">> mRestartPosition = $mRestartPosition")
            Trace.debug(">> vwSettingDim.visibility = ${mBinding.vwSettingDim.visibility}")
            Trace.debug(">> currentQuality = $mnCurrentQuality")
            Trace.debug(">> mbSubtitleEnabled = $mbSubtitleEnabled")
            Trace.debug(">> mbShowSettingView = $mbShowSettingView")
            Trace.debug(">> mbShowChannelView = $mbShowChannelView")
        }
    }

    override fun onStart() {
        Trace.debug("++ onStart()")
        super.onStart()

        if (mBinding.vwSettingDim.visibility == View.VISIBLE) return

        if (mVideo == null) return

        if (mPlayer == null) initPlayer()

        mBinding.vvPlayer.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Trace.debug("++ onSaveInstanceState()")

        Trace.debug(">> mRestartPosition = $mRestartPosition")
        Trace.debug(">> vwSettingDim.visibility = ${mBinding.vwSettingDim.visibility}")
        Trace.debug(">> currentQuality = $mnCurrentQuality")
        Trace.debug(">> mbSubtitleEnabled = $mbSubtitleEnabled")
        Trace.debug(">> mbShowSettingView = $mbShowSettingView")
        Trace.debug(">> mbShowChannelView = $mbShowChannelView")

        outState.putLong("CURRENT_POSITION", mRestartPosition)
        outState.putInt("CURRENT_QUALITY", mnCurrentQuality)
        outState.putBoolean("SHOW_SUBTITLE", mbSubtitleEnabled)
        outState.putInt("SETTING_DIM_VISIBLE", mBinding.vwSettingDim.visibility)
        outState.putBoolean("SHOW_SETTING", mbShowSettingView)
        outState.putBoolean("SHOW_CHANNEL", mbShowChannelView)
    }

    override fun onNewIntent(intent: Intent?) {
        Trace.debug("++ onNewIntent()")
        super.onNewIntent(intent)

        val newVideo: Video? = intent?.getParcelableExtra<Video>("param")

        if (newVideo != null && newVideo.showUrl != mVideo?.showUrl) {
            Trace.debug(">> onNewIntent() strUrl = ${newVideo.showUrl}")
            releasePlayer()
            mVideo = newVideo
        }
    }

    override fun onResume() {
        Trace.debug("++ onResume() orientation = ${resources.configuration.orientation}")
        super.onResume()

        mBinding.orientation = resources.configuration.orientation

        if (mVideo == null) return

        if (mBinding.vwSettingDim.visibility == View.VISIBLE) return

        if (mPlayer == null) initPlayer()

        startPlayer()
    }

    override fun onPause() {
        Trace.debug("++ onPause()")
        super.onPause()

        if (mBinding.vwSettingDim.visibility == View.VISIBLE) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isInPictureInPictureMode) {
                Trace.debug(">> onPause() PictureInPictureMode true")
                VideoPIPManager.getInstance().notifyModeChanged(isInPictureInPictureMode)
            } else {
                Trace.debug(">> onPause() PictureInPictureMode false")
                pausePlayer()
            }
        }
    }

    override fun onStop() {
        Trace.debug("++ onStop()")
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        Trace.debug("++ onDestroy()")
        super.onDestroy()
        releasePlayer()
    }

    private fun startPlayer() {
        if (mBinding.vwSettingDim.visibility == View.VISIBLE) return

        if (mPlayer == null) initPlayer()

        mPlayer!!.play()
        mBinding.vvPlayer.onResume()
        toggleIcon(mBinding.ivPlay, mBinding.ivPause)
        mbCheckPlay = true
        mnFadeoutCount = FADEOUT_SECONDS
        updatePlayer()
    }

    private fun pausePlayer() {
        mPlayer!!.pause()
        mBinding.vvPlayer.onPause()
        toggleIcon(mBinding.ivPause, mBinding.ivPlay)
        updateSeekBar()
        mRestartPosition = mPlayer!!.contentPosition
        mbCheckPlay = false

        if (::mCheckPlayJob.isInitialized && mCheckPlayJob.isActive) {
            mCheckPlayJob.cancel()
        }
    }

    private fun releasePlayer() {
        mBinding.vvPlayer.onPause()
        mbCheckPlay = false

        if (::mCheckPlayJob.isInitialized && mCheckPlayJob.isActive) {
            mCheckPlayJob.cancel()
        }

        mPlayer?.release()
        mPlayer = null
        mBinding.vvPlayer.player = null
    }

    private fun updatePlayer() {
        Trace.debug("++ updatePlayer()")

        if (::mCheckPlayJob.isInitialized && mCheckPlayJob.isActive) return

        mCheckPlayJob = CoroutineScope(Dispatchers.Main).launch {
            while (mbCheckPlay) {
//                Trace.debug(">> checkPlayable() updateSeekBar()")
                updateSeekBar()
                delay(CONTROLLER_UPDATE_MS)
            }
        }
    }

    private fun initPlayer() {
        if (mPlayer == null) {
            mPlayer = SimpleExoPlayer.Builder(baseContext)
//                    .setMediaSourceFactory(mediaSourceFactory)
                    .setTrackSelector(DefaultTrackSelector(baseContext))
                    .build()

            mBinding.vvPlayer.player = mPlayer

//            mPlayer?.addTextOutput { Trace.debug("++ onCues()") }

            mPlayer?.addListener(object: Player.EventListener {
//                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//                    Trace.debug("++ onPlayerStateChanged() playWhenReady = $playWhenReady playbackState = $playbackState")
//                    super.onPlayerStateChanged(playWhenReady, playbackState)
//                }

                override fun onPlaybackStateChanged(state: Int) {
                    Trace.debug("++ onPlaybackStateChanged() state = $state")
                    super.onPlaybackStateChanged(state)

                    when (state) {
                        Player.STATE_IDLE -> Trace.debug(">> Player.STATE_IDLE")
                        Player.STATE_BUFFERING -> Trace.debug(">> Player.STATE_BUFFERING")

                        Player.STATE_READY -> {
                            Trace.debug(">> Player.STATE_READY")
//                            changeResolution(mnCurrentQuality)
//                            showSubtitle(mbSubtitleEnabled)
//                            showClosedCaption(mbSubtitleEnabled)
                        }

                        Player.STATE_ENDED -> {
                            Trace.debug(">> Player.STATE_ENDED")
                            pausePlayer()
                        }
                    }
                }

//                override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
//                    Trace.debug("++ onPlayWhenReadyChanged() playWhenReady = $playWhenReady reason = $reason")
//                    super.onPlayWhenReadyChanged(playWhenReady, reason)
//                }
//
//                override fun onIsPlayingChanged(isPlaying: Boolean) {
//                    Trace.debug("++ onIsPlayingChanged() isPlaying = $isPlaying")
//                    super.onIsPlayingChanged(isPlaying)
//                }

                override fun onPlayerError(error: ExoPlaybackException) {
                    Trace.debug("++ onPlayerError() error = ${error.printStackTrace()}")
                    super.onPlayerError(error)
                }
            })
        }

        if (mRestartPosition > 0) {
            mPlayer?.seekTo(mRestartPosition)
        }

        mPlayer?.setMediaItem(createMediaItem())
        mPlayer?.prepare()
        mPlayer?.playWhenReady = true

        mPlayer?.addVideoListener(object : VideoListener {
            override fun onRenderedFirstFrame() {
                Trace.debug("++ onRenderedFirstFrame()")
                super.onRenderedFirstFrame()
                analyzeMedia()
                showClosedCaption(mbSubtitleEnabled)
                showSubtitle(mbSubtitleEnabled)
                changeResolution(mnCurrentQuality)
                initSeekBar()
                startPlayer()
            }
        })
    }

    private fun createMediaItem(): MediaItem {
        var subTitle: MediaItem.Subtitle? = null

        if (!mVideo!!.showSubtitlUrl.isNullOrEmpty()) {
            val strMimeTypes = when {
                mVideo!!.showSubtitlUrl.endsWith(SUBTITLE_TTML) -> MimeTypes.APPLICATION_TTML
                mVideo!!.showSubtitlUrl.endsWith(SUBTITLE_WEBVVT) -> MimeTypes.TEXT_VTT
                mVideo!!.showSubtitlUrl.endsWith(SUBTITLE_SUBRIP) -> MimeTypes.APPLICATION_SUBRIP
                else -> MimeTypes.APPLICATION_SUBRIP
            }

            subTitle = MediaItem.Subtitle(
                    Uri.parse(mVideo!!.showSubtitlUrl),
                    strMimeTypes,
                    "en",
                    Format.NO_VALUE)
        }

        val mediaItemBuilder = MediaItem.Builder().setUri(mVideo!!.showUrl)

        if (subTitle != null) mediaItemBuilder.setSubtitles(Lists.newArrayList(subTitle))

        return mediaItemBuilder.build()
    }

    private fun playWithSubtitle() {
        val subTitle: MediaItem.Subtitle = MediaItem.Subtitle(
                Uri.parse("https://tvoemstorage.blob.core.windows.net/lgtv/cc/OEMLGTV_LUMINESSSILKV111E1PIHD_115669_800-314-6659.srt"),
//                Uri.parse("https://cdn.jwplayer.com/tracks/PZHiJeZE.webvtt"),
                MimeTypes.APPLICATION_SUBRIP,
//                MimeTypes.TEXT_VTT,
                "en",
                Format.NO_VALUE)

        val item: MediaItem = MediaItem.Builder()
//            .setUri("https://8c3c612ee3e94223983199a1a3182bc2.mediatailor.us-east-1.amazonaws.com/v1/master/44f73ba4d03e9607dcd9bebdcb8494d86964f1d8/test-LG-BloombergUHD-01/playlist.m3u8")
//            .setUri("https://hls.xumo.com/channel-hls/v1/b6ede872c0924fca/9993301/master.m3u8")
//            .setUri("https://vdqvcus.akamaized.net/6213337444001.m3u8")
            .setUri("https://vod-301f.kxcdn.com/OEMLGTV_LUMINESSSILKV111E1PIHD_115669_800-314-6659.mp4")
//            .setUri("https://vod-301f.kxcdn.com/OEMLGTV_POPGOESTHE70S5P\$26.99PIHD_115429_800-807-7741_carbon.mp4")
//            .setUri("https://vod-301f.kxcdn.com/OEMLGTV_LUMINESSSILKV111E1PIHD_115669_800-314-6659.mp4")
            .setSubtitles(Lists.newArrayList(subTitle))
            .build()

//        val item: MediaItem = MediaItem.fromUri("https://5a0388bda0d20.streamlock.net/vod/_definst_/SMIL:Shows/JTV_BCAST_2021-02-18-00/JTV_BCAST_2021-02-18-00.smil/Playlist.m3u8")

//        val factory = DefaultDataSourceFactory(baseContext)
//        val subtitleMediaSource = SingleSampleMediaSource.Factory(factory).createMediaSource(subTitle, C.TIME_UNSET)
//
//        val mediaSource = ProgressiveMediaSource.Factory(factory).createMediaSource(item)
//        val mediaSource = HlsMediaSource.Factory(factory).createMediaSource(item)

//        val mergeSource = MergingMediaSource(mediaSource, subtitleMediaSource)

//        exoPlayer.setMediaSource(mergeSource)
        mPlayer?.setMediaItem(item)
        mPlayer?.prepare()
        mPlayer?.play()
    }

    private fun showSubtitle(bShow: Boolean) {
        if (mVideo!!.showSubtitlUrl.isNullOrEmpty()) return

        val trackSelector = mPlayer?.trackSelector as DefaultTrackSelector

        if (bShow) {
            trackSelector.parameters = trackSelector.parameters.buildUpon()
                    .setPreferredTextLanguage("en")
                    .build()
        } else {
            trackSelector.parameters = trackSelector.parameters.buildUpon()
                    .setDisabledTextTrackSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                    .build()
        }
    }

    private fun showClosedCaption(bShow: Boolean) {
        Trace.debug("++ showClosedCaption()")
        if (mnTextTrackGroupIndex == -1) return

        val trackSelector = mPlayer?.trackSelector as DefaultTrackSelector
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        val trackGroups = mappedTrackInfo!!.getTrackGroups(mnTextTrackGroupIndex)
        val currentTrackSelection = mPlayer!!.currentTrackSelections[mnTextTrackGroupIndex]

        if (!bShow) {
            trackSelector.parameters = trackSelector.parameters.buildUpon()
                    .setRendererDisabled(mnTextTrackGroupIndex, true)
                    .clearSelectionOverrides()
                    .build()

            Trace.debug("-- showClosedCaption() clearSelectionOverrides()")
            return
        }

        Trace.debug(">> trackGroups.length = " + trackGroups.length)

        // fixme until trackGroups.length
//        for (groupIndex in 0 until trackGroups.length) {
        for (groupIndex in 0 until 1) {
            val group = trackGroups[groupIndex]
            Trace.debug(">> group.length = " + group.length)

            for (trackIndex in 0 until group.length) {
                val trackFormat = group.getFormat(trackIndex)
                val override = SelectionOverride(groupIndex, trackIndex)

                Trace.debug(">> currentTrackSelection.getFormat = ${currentTrackSelection?.getFormat(trackIndex)}")
                Trace.debug(">> currentTrackSelection.sampleMimeType = ${currentTrackSelection?.getFormat(trackIndex)?.sampleMimeType}")
                Trace.debug(">> trackFormat = $trackFormat")
                Trace.debug(">> trackFormat.sampleMimeType = ${trackFormat.sampleMimeType}")

                // fixme compare with media type and override supported CC
//                if (currentTrackSelection != null && currentTrackSelection.getFormat(trackIndex).sampleMimeType == trackFormat.sampleMimeType) {
                    Trace.debug(">> override Format = $trackFormat")
                    trackSelector.parameters = trackSelector.parameters.buildUpon()
                            .setPreferredTextLanguage("en")
                            .setRendererDisabled(mnTextTrackGroupIndex, false)
                            .setSelectionOverride(mnTextTrackGroupIndex, trackGroups, override)
                            .build()
//                }
            }
        }

        Trace.debug("-- showClosedCaption()")
    }

    private fun changeResolution(nQualityIndex: Int) {
        Trace.debug("++ changeResolution() nQualityIndex = $nQualityIndex")
        if (nQualityIndex == -1) return

        mnCurrentQuality = nQualityIndex
        val trackSelector = mPlayer?.trackSelector as DefaultTrackSelector
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        val trackGroups = mappedTrackInfo!!.getTrackGroups(mnVideoTrackGroupIndex)
        Trace.debug(">> trackGroups.length = ${trackGroups.length}")

        for (groupIndex in 0 until trackGroups.length) {
            val group = trackGroups[groupIndex]
            Trace.debug(">> group.length = ${group.length}")

            for (trackIndex in 0 until group.length) {
                Trace.debug(">> trackIndex = $trackIndex nQualityIndex = $nQualityIndex")

                if (trackIndex == nQualityIndex) {
                    Trace.debug(">> override groupIndex = $groupIndex mnVideoTrackGroupIndex = $mnVideoTrackGroupIndex")
                    val override = SelectionOverride(groupIndex, trackIndex)

                    trackSelector.parameters = trackSelector.parameters.buildUpon()
                            .setSelectionOverride(mnVideoTrackGroupIndex, trackGroups, override)
                            .build()

                    return
                }
            }
        }

        Trace.debug("-- changeResolution()")
    }

    private fun Int.getTimeString(): String {
        return String.format("%02d:%02d:%02d", this / 3600, (this / 60) % 60, this % 60)
    }

    private fun Long.getTimeString(): String {
        val nTime = (this / 1000L).toInt()
        return String.format("%02d:%02d:%02d", nTime / 3600, (nTime / 60) % 60, nTime % 60)
    }

    private fun initSeekBar() {
        Trace.debug("++ initSeekBar totalTime = ${mPlayer!!.contentDuration}")
        mBinding.sbProgress.max = (mPlayer!!.contentDuration / 1000L).toInt()

        if (mVideo!!.isLive()) {
            mnElapsedTime = 0
            mBinding.sbProgress.progress = mBinding.sbProgress.max
        } else {
            mBinding.sbProgress.progress = 0
            mBinding.tvTotalTime.text = mPlayer!!.contentDuration.getTimeString()
        }

        mBinding.sbProgress.apply {
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                    Trace.debug("++ onProgressChanged()")
                    if (!fromUser) return

                    mPlayer!!.seekTo(progress.toLong() * 1000L)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
        }
    }

    private fun updateSeekBar() {
        if (mVideo!!.isLive()) {
            mBinding.tvElapsedTime.text = mnElapsedTime++.getTimeString()
        } else {
            mBinding.tvElapsedTime.text = mPlayer!!.contentPosition.getTimeString()
            mBinding.sbProgress.progress = (mPlayer!!.contentPosition / 1000L).toInt()
            mBinding.sbProgress.secondaryProgress = mPlayer!!.bufferedPercentage
        }

        Trace.debug("++ updateSeekBar curr = ${mBinding.tvElapsedTime.text} " +
                "prog1 = ${mBinding.sbProgress.progress} " +
                "prog2 = ${mBinding.sbProgress.secondaryProgress} " +
                "total = ${mPlayer!!.contentDuration.getTimeString()} ")

        if (mnFadeoutCount == 0) {
            mnFadeoutCount = -1
            fadeControlView(false, null)
        } else if (mnFadeoutCount > 0) {
            mnFadeoutCount--
        }
    }

    private fun fadeControlView(bShow: Boolean, fEndTrans: ((Unit) -> (Unit))?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (super.isInPictureInPictureMode()) {
                return
            }
        }

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
        mBinding.clControlView.visibility = if (bShow) View.VISIBLE else View.GONE
    }

    private fun toggleIcon(to: View, from: View) {
        to.visibility = View.GONE
        from.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        Trace.debug("++ onBackPressed() fragment size =  ${supportFragmentManager.fragments.size}")

        var nFragmentSize = supportFragmentManager.fragments.size

        if (nFragmentSize > 0) {
            supportFragmentManager.commit {
                if (mBinding.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                } else {
                    setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                }

                remove(supportFragmentManager.fragments[--nFragmentSize])
            }

            if (nFragmentSize == 0) {
                mBinding.vwSettingDim.visibility = View.GONE
                startPlayer()
                return
            }

            return
        }

        super.onBackPressed()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        Trace.debug(">> onConfigurationChanged() newConfig.orientation = ${newConfig.orientation}")
        super.onConfigurationChanged(newConfig)

        mBinding.orientation = newConfig.orientation

//        supportFragmentManager.fragments.forEach {
//            supportFragmentManager.commit {
//                remove(it)
//            }
//        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        Trace.debug("++ onPictureInPictureModeChanged() mode = $isInPictureInPictureMode config = $newConfig")
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        VideoPIPManager.getInstance().notifyModeChanged(isInPictureInPictureMode)

        if (isInPictureInPictureMode) {
            mBinding.clControlView.visibility = View.GONE
        } else {
            mBinding.clControlView.visibility = View.VISIBLE
        }
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
//                        val aspectRatio: Rational = Rational(mMediaPlayer!!.videoWidth, mMediaPlayer!!.videoHeight)
//                        val pipParams: PictureInPictureParams = PictureInPictureParams.Builder().setAspectRatio(aspectRatio).build()
//                        enterPictureInPictureMode(pipParams)
                        enterPictureInPictureMode(PictureInPictureParams.Builder().build())
                    }
                }
            }

            R.id.iv_setting -> {
                if (mBinding.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    ObjectAnimator.ofFloat(mBinding.clSettingView, "translationY", mBinding.clSettingView.height * -1f).apply {
                        duration = 300
                        start()
                    }
                } else if (mBinding.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    ObjectAnimator.ofFloat(mBinding.clSettingView, "translationX", mBinding.clSettingView.width * -1f).apply {
                        duration = 300
                        start()
                    }
                }

                mBinding.vwSettingDim.visibility = View.VISIBLE
//                mBinding.swSettingCc.isChecked = mbSubtitleEnabled
                pausePlayer()
                mbShowSettingView = true
            }

            R.id.iv_setting_close -> {
                if (mBinding.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    ObjectAnimator.ofFloat(mBinding.clSettingView, "translationY", mBinding.clSettingView.height * 1f).apply {
                        duration = 300
                        start()
                    }
                } else {
                    ObjectAnimator.ofFloat(mBinding.clSettingView, "translationX", mBinding.clSettingView.width * 1f).apply {
                        duration = 300
                        start()
                    }
                }

                if (mBinding.currentQuality != mBinding.rgQuality.checkedRadioButtonId) {
                    changeResolution(when (mBinding.rgQuality.checkedRadioButtonId) {
                        R.id.rb_quality_sd -> mnQualitySDIndex
                        R.id.rb_quality_hd -> mnQualityHDIndex
                        R.id.rb_quality_fhd -> mnQualityFHDIndex
                        else -> -1
                    })
                }

                if (mbSubtitleEnabled != mBinding.swSettingCc.isChecked) {
                    mbSubtitleEnabled = mBinding.swSettingCc.isChecked
                    showClosedCaption(mbSubtitleEnabled)
                    showSubtitle(mbSubtitleEnabled)
                }

                mBinding.vwSettingDim.visibility = View.GONE
                startPlayer()
                mbShowSettingView = false
            }

            R.id.ll_channel -> {
                if (mVideo?.isLive() == false) {
                    Trace.debug("onClick() video.isLive() = ${mVideo?.isLive()}")
                    return
                }

                if (mBinding.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    ObjectAnimator.ofFloat(mBinding.clChannelView, "translationY", mBinding.clChannelView.height * -1f).apply {
                        duration = 300
                        start()
                    }
                } else {
                    ObjectAnimator.ofFloat(mBinding.clChannelView, "translationX", mBinding.clChannelView.width * -1f).apply {
                        duration = 300
                        start()
                    }
                }

                mBinding.vwSettingDim.visibility = View.VISIBLE
                pausePlayer()
                mViewModel.requestData(mVideo!!)
                mbShowChannelView = true
            }

            R.id.iv_channel_close -> {
                if (mBinding.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    ObjectAnimator.ofFloat(mBinding.clChannelView, "translationY", mBinding.clChannelView.height * 1f).apply {
                        duration = 300
                        start()
                    }
                } else {
                    ObjectAnimator.ofFloat(mBinding.clChannelView, "translationX", mBinding.clChannelView.width * 1f).apply {
                        duration = 300
                        start()
                    }
                }

                mBinding.vwSettingDim.visibility = View.GONE
                startPlayer()
                mbShowChannelView = false
            }

            R.id.iv_menu_portrait -> {
                mBinding.vwSettingDim.visibility = View.VISIBLE
                pausePlayer()

                if (!mVideo?.productInfos.isNullOrEmpty()) {
                    Trace.debug(">> mVideo.productInfos.size = ${mVideo?.productInfos?.size}")

                    mVideo?.productInfos?.forEach {
                        it.patnrId = mVideo!!.patnrId
                        it.patncLogoPath = mVideo!!.patncLogoPath
                    }

                    val bundle: Bundle = Bundle()
                    bundle.putParcelableArrayList("products", mVideo?.productInfos)

                    supportFragmentManager.commit {
                        setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                        setReorderingAllowed(true)
                        add<PlayerPopupFragment>(R.id.fc_product_list, null, bundle)
                    }
                } else {
                    supportFragmentManager.commit {
                        setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                        setReorderingAllowed(true)
                        add<PlayerPopupFragment>(R.id.fc_product_list)
                    }

                    mViewModel.requestData(mVideo!!)
                }
            }

            R.id.iv_menu_landscape -> {
                mBinding.vwSettingDim.visibility = View.VISIBLE
                pausePlayer()

                if (!mVideo?.productInfos.isNullOrEmpty()) {
                    Trace.debug(">> mVideo.productInfos.size = ${mVideo?.productInfos?.size}")

                    mVideo?.productInfos?.forEach {
                        it.patnrId = mVideo!!.patnrId
                        it.patncLogoPath = mVideo!!.patncLogoPath
                    }

                    val bundle: Bundle = Bundle()
                    bundle.putParcelableArrayList("products", mVideo?.productInfos)

                    supportFragmentManager.commit {
                        setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        setReorderingAllowed(true)
                        add<PlayerPopupFragment>(R.id.fc_product_list, null, bundle)
                    }
                } else {
                    supportFragmentManager.commit {
                        setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                        setReorderingAllowed(true)
                        add<PlayerPopupFragment>(R.id.fc_product_list)
                    }

                    mViewModel.requestData(mVideo!!)
                }
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

    override fun onClick(v: View, pos: Int) {
        Trace.debug("++ onClick() v = ${v.id} pos = $pos")
    }

    override fun onItemClick(parent: View, parentPos: Int, item: View, pos: Int) {
        Trace.debug("++ onItemClick() parent = ${parent.id} parentPos = $parentPos item = ${item.id} pos = $pos")

        if (mViewModel.mldVideoProduct.value!!.showInfos.size <= 0) return

        if (mVideo?.showUrl == mViewModel.mldVideoProduct.value!!.showInfos[pos].showUrl) return

        releasePlayer()
        mVideo = mViewModel.mldVideoProduct.value!!.showInfos[pos]
        mBinding.viewdata = mVideo
        initPlayer()

        ObjectAnimator.ofFloat(mBinding.clChannelView, "translationY", mBinding.clChannelView.height * 1f).apply {
            duration = 300
            start()
        }

        mBinding.vwSettingDim.visibility = View.GONE
    }

//    fun onRadioButtonChanged(radioGroup: RadioGroup, id: Int) {
//        Trace.debug("++ onRadioButtonChanged() id = $id")
//
//        when (id) {
//            R.id.rb_quality_sd -> {}
//            R.id.rb_quality_hd -> {}
//            R.id.rb_quality_fhd -> {}
//        }
//    }
//
//    fun onSwitchChanged(switch: CompoundButton, checked: Boolean) {
//        Trace.debug("++ onSwitchChanged() checked = $checked")
//    }

    // TrackInfo
    // - TrackGroupArray[]
    //   - TrackGroup[]
    //     - Format[]
    fun analyzeMedia() {
        Trace.debug("++ analyzeMedia()")

        if (mPlayer == null) {
            Trace.debug("-- analyzeMedia() mPlayer is null")
            return
        }

        mnVideoTrackGroupIndex = -1
        mnTextTrackGroupIndex = -1
        mnQualitySDIndex = -1
        mnQualityHDIndex = -1
        mnQualityFHDIndex = -1
        var currentWidth: Int? = 0

        Trace.debug("\t>> rendererCount = " + mPlayer?.rendererCount)
        Trace.debug("\t>> currentTrackSelections.length = ${mPlayer!!.currentTrackSelections.length}")
        Trace.debug("\t>> currentTrackGroups.length = ${mPlayer!!.currentTrackGroups.length}")
        Trace.debug("\t>> currentStaticMetadata.size = ${mPlayer!!.currentStaticMetadata.size}")
        Trace.debug("\t>> videoFormat = ${mPlayer!!.videoFormat}")
        currentWidth = mPlayer!!.videoFormat?.width
        Trace.debug("\t>> audioFormat = ${mPlayer!!.audioFormat}")
        Trace.debug("\t>> contentDuration = ${mPlayer!!.contentDuration}")
        Trace.debug("\t>> currentPosition = ${mPlayer!!.currentPosition}")
        Trace.debug("\t>> contentBufferedPosition = ${mPlayer!!.contentBufferedPosition}")
        Trace.debug("\t>> bufferedPercentage = ${mPlayer!!.bufferedPercentage}")
        Trace.debug("\t>> currentTimeline = ${mPlayer!!.currentTimeline}")
        val timeline: Timeline = mPlayer!!.currentTimeline
        Trace.debug("\t\t>> timeline.periodCount = ${timeline.periodCount}")
        Trace.debug("\t\t>> timeline.windowCount = ${timeline.windowCount}")
        Trace.debug("\t>> deviceInfo = ${mPlayer!!.deviceInfo}")
        Trace.debug("\t>> clock = ${mPlayer!!.clock}")
        val clock: Clock = mPlayer!!.clock
        Trace.debug("\t\t>> clock.elapsedRealtime() = ${clock.elapsedRealtime()}")
        Trace.debug("\t>> currentManifest = ${mPlayer!!.currentManifest}")
        Trace.debug("\t>> isLoading = ${mPlayer!!.isLoading}")
        Trace.debug("\t>> isPlaying = ${mPlayer!!.isPlaying}")
        Trace.debug("\t>> isPlayingAd = ${mPlayer!!.isPlayingAd}")
        Trace.debug("\t>> currentMediaItem = ${mPlayer!!.currentMediaItem}")
        Trace.debug("\t>> metadataComponent = ${mPlayer!!.metadataComponent}")

        val trackSelector: MappingTrackSelector = mPlayer!!.trackSelector as MappingTrackSelector
        val mappedTrackInfo: MappingTrackSelector.MappedTrackInfo = trackSelector.currentMappedTrackInfo?: return

        for (i in 0 until mappedTrackInfo.rendererCount) {
            val trackGroups: TrackGroupArray = mappedTrackInfo.getTrackGroups(i)
            Trace.debug(">> trackGroups.length = ${trackGroups.length}")

            if (trackGroups.length == 0) continue
            Trace.debug(">> TrackGroupArray index = $i")

            for (j in 0 until trackGroups.length) {
                val trackGroup: TrackGroup = trackGroups.get(j)
                Trace.debug(">> TrackGroup index = $j")

                when (mPlayer?.getRendererType(i)) {
                    C.TRACK_TYPE_AUDIO -> {
                        Trace.debug(">> TRACK_TYPE_AUDIO trackGroup[0] length = ${trackGroup.length}")

                        for (j in 0 until trackGroup.length) {
                            val format = trackGroup.getFormat(j)
                            Trace.debug("\t>> audioTrackFormat[$j] = $format")
                        }
                    }

                    C.TRACK_TYPE_VIDEO -> {
                        mnVideoTrackGroupIndex = i
                        Trace.debug(">> TRACK_TYPE_VIDEO trackGroup[0] length = ${trackGroup.length}")
                        Trace.debug(">> mnVideoTrackGroupIndex = $mnVideoTrackGroupIndex")
                        val formats: ArrayList<Pair<Int, Format>> = ArrayList()

                        for (k in 0 until trackGroup.length) {
                            val format = trackGroup.getFormat(k)
                            Trace.debug("\t>> videoTrackFormat[$k] = $format")
                            Trace.debug("\t>> format.label = " + format.label)
                            Trace.debug("\t>> format.language = " + format.language)
                            Trace.debug("\t>> format.sampleMimeType = " + format.sampleMimeType)
                            Trace.debug("\t>> format.width = " + format.width)
                            Trace.debug("\t>> format.height = " + format.height)
                            Trace.debug("\t>> format.pixelWidthHeightRatio = " + format.pixelWidthHeightRatio)
                            Trace.debug("\t>> format.codecs = " + format.codecs)
                            Trace.debug("\t>> format.frameRate = " + format.frameRate)
                            Trace.debug("\t>> format.bitrate = " + format.bitrate)
                            Trace.debug("\t>> format.averageBitrate = " + format.averageBitrate)
                            Trace.debug("\t>> format.channelCount = " + format.channelCount)
                            Trace.debug("\t>> format.accessibilityChannel = " + format.accessibilityChannel)
                            Trace.debug("\t>> format.metadata = " + format.metadata)

                            if (trackGroup.length < 3) {
                                when (format.width) {
                                    in 320..720 -> {    // 720 x 480
//                                if (format.height == 480)
                                        mnQualitySDIndex = k
                                        if (currentWidth == format.width) mBinding.currentQuality = R.id.rb_quality_sd
                                        Trace.debug("\t>> mnQualitySDIndex = $mnQualitySDIndex")
                                    }

                                    in 721..1280 -> {   // 1280 x 720
//                                if (format.height == 720)
                                        mnQualityHDIndex = k
                                        if (currentWidth == format.width) mBinding.currentQuality = R.id.rb_quality_hd
                                        Trace.debug("\t>> mnQualityHDIndex = $mnQualityHDIndex")
                                    }

                                    in 1281..1920 -> {   // 1920 x 1080
//                                if (format.height == 1080)
                                        mnQualityFHDIndex = k
                                        if (currentWidth == format.width) mBinding.currentQuality = R.id.rb_quality_fhd
                                        Trace.debug("\t>> mnQualityFHDIndex = $mnQualityFHDIndex")
                                    }

                                    // 2560 x 1440
                                }
                            } else {
                                formats.add(Pair(k, format))
                            }
                        }

                        if (trackGroup.length >= 3) {
                            Trace.debug(">> trackGroup sort and assign quality")
                            formats.sortBy { it.second.width }

                            mnQualitySDIndex = formats[0].first
                            mnQualityHDIndex = formats[formats.lastIndex / 2].first
                            mnQualityFHDIndex = formats.last().first

                            mBinding.currentQuality = when (currentWidth) {
                                formats[0].second.width -> R.id.rb_quality_sd
                                formats[formats.lastIndex / 2].second.width -> R.id.rb_quality_hd
                                formats.last().second.width -> R.id.rb_quality_fhd
                                else -> R.id.rb_quality_hd
                            }

//                        formats.forEach {
//                            Trace.debug(">> it.width = ${it.second.width}")
//                        }

                            Trace.debug(">> mnQualitySDIndex = $mnQualitySDIndex ${formats[0].second.width} x ${formats[0].second.height}")
                            Trace.debug(">> mnQualityHDIndex = $mnQualityHDIndex ${formats[formats.lastIndex / 2].second.width} x ${formats[formats.lastIndex / 2].second.height}")
                            Trace.debug(">> mnQualityFHDIndex = $mnQualityFHDIndex ${formats.last().second.width} x ${formats.last().second.height}")
                        }

                        mBinding.qualitySD = mnQualitySDIndex
                        mBinding.qualityHD = mnQualityHDIndex
                        mBinding.qualityFHD = mnQualityFHDIndex
                    }

                    C.TRACK_TYPE_TEXT -> {
                        mnTextTrackGroupIndex = i
                        mBinding.swSettingCc.isEnabled = true
                        Trace.debug(">> TRACK_TYPE_TEXT trackGroup[0] length = ${trackGroup.length}")
                        Trace.debug(">> mnTextTrackGroupIndex = $mnTextTrackGroupIndex")

                        for (k in 0 until trackGroup.length) {
                            val format = trackGroup.getFormat(k)
                            Trace.debug("\t>> textTrackFormat[$k] = $format")
                        }
                    }

                    C.TRACK_TYPE_METADATA -> {
                        Trace.debug(">> TRACK_TYPE_METADATA trackGroup[0] length = ${trackGroup.length}")

                        for (k in 0 until trackGroup.length) {
                            val format = trackGroup.getFormat(k)
                            Trace.debug("\t>> metaTrackFormat[$k] = $format")
                        }
                    }

                    C.TRACK_TYPE_UNKNOWN -> {
                        Trace.debug(">> TRACK_TYPE_UNKNOWN trackGroup[0] length = ${trackGroup.length}")
                    }
                    C.TRACK_TYPE_DEFAULT -> {
                        Trace.debug(">> TRACK_TYPE_DEFAULT trackGroup[0] length = ${trackGroup.length}")
                    }
                    C.TRACK_TYPE_IMAGE -> {
                        Trace.debug(">> TRACK_TYPE_IMAGE trackGroup[0] length = ${trackGroup.length}")
                    }
                    C.TRACK_TYPE_CAMERA_MOTION -> {
                        Trace.debug(">> TRACK_TYPE_CAMERA_MOTION trackGroup[0] length = ${trackGroup.length}")
                    }

                    else -> {
                        Trace.debug(">> else trackGroup[0] length = ${trackGroup.length}")
                    }
                }
            }
        }

        Trace.debug("-- analyzeMedia()")
    }

    private fun onDataListChanged(itemList: WatchNow.Response.Data) {
        Trace.debug("++ onDataListChanged()")

        if (itemList.showInfos.size > 0) {
            itemList.showInfos.forEach {
                it.selected = (it.showUrl == mVideo?.showUrl)

                when (it.patnrId.toInt()) {
                    1 -> { it.resourceId = R.drawable.sel_pop_qvc }     // QVC
                    2 -> { it.resourceId = R.drawable.sel_pop_hsn }     // HSN
                    3 -> { it.resourceId = R.drawable.sel_pop_jtv }     // JTV
                    4 -> { it.resourceId = R.drawable.sel_pop_ontv }    // onTV
                }
            }

            mBinding.compList.setItemList(itemList.showInfos)
            mBinding.compList.addItemListener(this)
        }
    }
}
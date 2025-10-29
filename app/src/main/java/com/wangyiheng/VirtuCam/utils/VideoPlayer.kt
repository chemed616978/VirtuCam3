package com.wangyiheng.VirtuCam.utils

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.Surface
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.util.Util
import com.wangyiheng.VirtuCam.MainHook.Companion.c2_reader_Surfcae
import com.wangyiheng.VirtuCam.MainHook.Companion.context
import com.wangyiheng.VirtuCam.MainHook.Companion.oriHolder
import com.wangyiheng.VirtuCam.MainHook.Companion.original_c1_preview_SurfaceTexture
import com.wangyiheng.VirtuCam.MainHook.Companion.original_preview_Surface
import com.wangyiheng.VirtuCam.utils.InfoProcesser.videoStatus
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

object VideoPlayer {
    var c2_hw_decode_obj: VideoToFrames? = null
    var exoPlayer: ExoPlayer? = null
    var mediaPlayer: MediaPlayer? = null
    var c3_player: MediaPlayer? = null
    var copyReaderSurface: Surface? = null
    var currentRunningSurface: Surface? = null
    private val scheduledExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    
    init {
        startTimerTask()
    }

    // 启动定时任务
    private fun startTimerTask() {
        scheduledExecutor.scheduleWithFixedDelay({
            performTask()
        }, 10, 10, TimeUnit.SECONDS)
    }

    // 实际执行的任务
    private fun performTask() {
        restartMediaPlayer()
    }

    fun restartMediaPlayer() {
        if (videoStatus?.isVideoEnable == true || videoStatus?.isLiveStreamingEnabled == true) return
        if (currentRunningSurface == null || currentRunningSurface?.isValid == false) return
        releaseMediaPlayer()
    }

    // RTMP流播放器初始化 باستخدام ExoPlayer
    fun initRTMPStreamPlayer() {
        context?.let { ctx ->
            exoPlayer = ExoPlayer.Builder(ctx).build().apply {
                // 设置媒体项
                val mediaItem = MediaItem.fromUri(videoStatus!!.liveURL)
                setMediaItem(mediaItem)

                // 错误监听器
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: Player.PlaybackException) {
                        Toast.makeText(context, "播放错误: ${error.message}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            Toast.makeText(context, "直播接收成功", Toast.LENGTH_SHORT).show()
                        }
                    }
                })

                // 准备播放器
                prepare()

                Toast.makeText(context, videoStatus!!.liveURL, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun initMediaPlayer(surface: Surface) {
        val volume = if (videoStatus?.volume == true) 1F else 0F
        mediaPlayer = MediaPlayer().apply {
            isLooping = true
            setSurface(surface)
            setVolume(volume, volume)
            setOnPreparedListener { start() }
            val videoPathUri = Uri.parse("content://com.wangyiheng.VirtuCam.videoprovider")
            context?.let { setDataSource(it, videoPathUri) }
            prepare()
        }
    }

    fun initializeTheStateAsWellAsThePlayer() {
        InfoProcesser.initStatus()

        if (exoPlayer == null) {
            if (videoStatus?.isLiveStreamingEnabled == true) {
                initRTMPStreamPlayer()
            }
        }
    }

    // 将surface传入进行播放
    private fun handleMediaPlayer(surface: Surface) {
        try {
            InfoProcesser.initStatus()

            videoStatus?.also { status ->
                if (!status.isVideoEnable && !status.isLiveStreamingEnabled) return

                val volume = if (status.volume) 1F else 0F

                when {
                    status.isLiveStreamingEnabled -> {
                        exoPlayer?.let { player ->
                            player.volume = volume
                            player.setVideoSurface(surface)
                            if (player.playbackState != Player.STATE_READY) {
                                player.play()
                            }
                        }
                    }
                    else -> {
                        mediaPlayer?.also {
                            if (it.isPlaying) {
                                it.setVolume(volume, volume)
                                it.setSurface(surface)
                            } else {
                                releaseMediaPlayer()
                                initMediaPlayer(surface)
                            }
                        } ?: run {
                            releaseMediaPlayer()
                            initMediaPlayer(surface)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logError("MediaPlayer Error", e)
        }
    }

    private fun logError(message: String, e: Exception) {
        Log.e("MediaPlayerHandler", "$message: ${e.message}")
    }

    fun releaseMediaPlayer() {
        // إيقاف وتحرير MediaPlayer
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        
        // إيقاف وتحرير ExoPlayer
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
    }

    fun camera2Play() {
        // 带name的surface
        original_preview_Surface?.let { surface ->
            handleMediaPlayer(surface)
        }

        // name=null的surface
        c2_reader_Surfcae?.let { surface ->
            c2_reader_play(surface)
        }
    }

    fun c1_camera_play() {
        if (original_c1_preview_SurfaceTexture != null) {
            original_preview_Surface = Surface(original_c1_preview_SurfaceTexture)
            if (original_preview_Surface!!.isValid == true) {
                handleMediaPlayer(original_preview_Surface!!)
            }
        }

        if (oriHolder?.surface != null) {
            original_preview_Surface = oriHolder?.surface
            if (original_preview_Surface!!.isValid == true) {
                handleMediaPlayer(original_preview_Surface!!)
            }
        }

        c2_reader_Surfcae?.let { surface ->
            c2_reader_play(surface)
        }
    }

    fun c2_reader_play(c2_reader_Surfcae: Surface) {
        if (c2_reader_Surfcae == copyReaderSurface) {
            return
        }

        copyReaderSurface = c2_reader_Surfcae

        if (c2_hw_decode_obj != null) {
            c2_hw_decode_obj!!.stopDecode()
            c2_hw_decode_obj = null
        }

        c2_hw_decode_obj = VideoToFrames()
        try {
            val videoUrl = "content://com.wangyiheng.VirtuCam.videoprovider"
            val videoPathUri = Uri.parse(videoUrl)
            c2_hw_decode_obj!!.setSaveFrames(OutputImageFormat.NV21)
            c2_hw_decode_obj!!.set_surface(c2_reader_Surfcae)
            c2_hw_decode_obj!!.decode(videoPathUri)
        } catch (e: Exception) {
            Log.d("dbb", e.toString())
        }
    }

    // دالة مساعدة للتحكم في ExoPlayer
    fun playExoPlayer() {
        exoPlayer?.play()
    }

    fun pauseExoPlayer() {
        exoPlayer?.pause()
    }

    fun stopExoPlayer() {
        exoPlayer?.stop()
    }

    fun isExoPlayerPlaying(): Boolean {
        return exoPlayer?.isPlaying ?: false
    }
}

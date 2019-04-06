package com.oheuropa.android.ui.intro

import android.content.Context
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.w
import java.io.IOException

class TextureVideoView(ctx: Context, attrs: AttributeSet) : TextureView(ctx, attrs),
    TextureView.SurfaceTextureListener {

    private var mediaPlayer: MediaPlayer? = null

    private var videoHeight: Float = 0F
    private var videoWidth: Float = 0F

    private var isDataSourceSet: Boolean = false
    private var isViewAvailable: Boolean = false
    private var isVideoPrepared: Boolean = false
    private var isPlayCalled: Boolean = false

    var scaleType: ScaleType? = null
    private var state: State? = null

    private fun log(message: String) = d { message }

    val duration: Int
        get() = mediaPlayer!!.duration

    private var mListener: MediaPlayerListener? = null

    enum class ScaleType { CENTER_CROP, TOP, BOTTOM }

    enum class State { UNINITIALIZED, PLAY, STOP, PAUSE, END }

    init {
        initPlayer()
        scaleType = ScaleType.CENTER_CROP
        surfaceTextureListener = this
    }

    private fun updateTextureViewSize() {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        var scaleX = 1.0f
        var scaleY = 1.0f

        if (videoWidth > viewWidth && videoHeight > viewHeight) {
            scaleX = videoWidth / viewWidth
            scaleY = videoHeight / viewHeight
        } else if (videoWidth < viewWidth && videoHeight < viewHeight) {
            scaleY = viewWidth / videoWidth
            scaleX = viewHeight / videoHeight
        } else if (viewWidth > videoWidth) {
            scaleY = viewWidth / videoWidth / (viewHeight / videoHeight)
        } else if (viewHeight > videoHeight) {
            scaleX = viewHeight / videoHeight / (viewWidth / videoWidth)
        }

        // Calculate pivot points, in our case crop from center
        val pivotPointX: Int
        val pivotPointY: Int

        when (scaleType) {
            TextureVideoView.ScaleType.TOP -> {
                pivotPointX = 0
                pivotPointY = 0
            }
            TextureVideoView.ScaleType.BOTTOM -> {
                pivotPointX = viewWidth.toInt()
                pivotPointY = viewHeight.toInt()
            }
            TextureVideoView.ScaleType.CENTER_CROP -> {
                pivotPointX = (viewWidth / 2).toInt()
                pivotPointY = (viewHeight / 2).toInt()
            }
            else -> {
                pivotPointX = (viewWidth / 2).toInt()
                pivotPointY = (viewHeight / 2).toInt()
            }
        }

        val matrix = Matrix()
        matrix.setScale(scaleX, scaleY, pivotPointX.toFloat(), pivotPointY.toFloat())

        setTransform(matrix)
    }

    private fun initPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        } else {
            mediaPlayer?.reset()
        }
        isVideoPrepared = false
        isPlayCalled = false
        state = State.UNINITIALIZED
    }

    fun setDataSource(context: Context, uri: Uri) {
        initPlayer()

        try {
            mediaPlayer?.setDataSource(context, uri)
            isDataSourceSet = true
            prepare()
        } catch (e: IOException) {
            w(e)
        }

    }

    private fun prepare() {
        try {
            mediaPlayer?.setOnVideoSizeChangedListener { _, width, height ->
                videoWidth = width.toFloat()
                videoHeight = height.toFloat()
                updateTextureViewSize()
            }
            mediaPlayer?.setOnCompletionListener {
                state = State.END
                log("Video has ended.")

                if (mListener != null) {
                    mListener?.onVideoEnd()
                }
            }

            // don't forget to call MediaPlayer.prepareAsync() method when you use constructor for
            // creating MediaPlayer
            mediaPlayer?.prepareAsync()

            // Play video when the media source is ready for playback.
            mediaPlayer?.setOnPreparedListener {
                isVideoPrepared = true
                if (isPlayCalled && isViewAvailable) {
                    log("Player is prepared and play() was called.")
                    play()
                }

                if (mListener != null) {
                    mListener?.onVideoPrepared()
                }
            }

        } catch (e: Exception) {
            w(e)
        }

    }

    /**
     * Play or resume video. Video will be played as soon as view is available and media player is
     * prepared.
     *
     * If video is stopped or ended and play() method was called, video will start over.
     */
    fun play() {
        if (!isDataSourceSet) {
            log("play() was called but data source was not set.")
            return
        }

        isPlayCalled = true

        if (!isVideoPrepared) {
            log("play() was called but video is not prepared yet, waiting.")
            return
        }

        if (!isViewAvailable) {
            log("play() was called but view is not available yet, waiting.")
            return
        }

        if (state == State.PLAY) {
            log("play() was called but video is already playing.")
            return
        }

        if (state == State.PAUSE) {
            log("play() was called but video is paused, resuming.")
            state = State.PLAY
            mediaPlayer?.start()
            return
        }

        if (state == State.END || state == State.STOP) {
            log("play() was called but video already ended, starting over.")
            state = State.PLAY
            mediaPlayer?.seekTo(0)
            mediaPlayer?.start()
            return
        }

        state = State.PLAY
        mediaPlayer?.start()
    }

    /**
     * Pause video. If video is already paused, stopped or ended nothing will happen.
     */
    fun pause() {
        if (state == State.PAUSE) {
            log("pause() was called but video already paused.")
            return
        }

        if (state == State.STOP) {
            log("pause() was called but video already stopped.")
            return
        }

        if (state == State.END) {
            log("pause() was called but video already ended.")
            return
        }

        state = State.PAUSE
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    /**
     * Stop video (pause and seek to beginning). If video is already stopped or ended nothing will
     * happen.
     */
    fun stop() {
        if (state == State.STOP) {
            log("stop() was called but video already stopped.")
            return
        }

        if (state == State.END) {
            log("stop() was called but video already ended.")
            return
        }

        state = State.STOP
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            mediaPlayer?.seekTo(0)
        }
    }

    /**
     * @see android.media.MediaPlayer.seekTo
     */
    fun seekTo(milliseconds: Int) {
        mediaPlayer?.seekTo(milliseconds)
    }

    /**
     * Listener trigger 'onVideoPrepared' and `onVideoEnd` events
     */
    fun setListener(listener: MediaPlayerListener) {
        mListener = listener
    }

    interface MediaPlayerListener {

        fun onVideoPrepared()

        fun onVideoEnd()
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int,
                                           height: Int) {
        val surface = Surface(surfaceTexture)
        mediaPlayer?.setSurface(surface)
        isViewAvailable = true
        if (isDataSourceSet && isPlayCalled && isVideoPrepared) {
            log("View is available and play() was called.")
            play()
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture) = false

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

    }
}

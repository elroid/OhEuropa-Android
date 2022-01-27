package com.oheuropa.android.data

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import androidx.core.net.toUri
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.v
import com.github.ajalt.timberkt.w
import com.oheuropa.android.R
import com.oheuropa.android.domain.*
import com.oheuropa.android.domain.AudioComponent.State.*
import com.oheuropa.android.util.ViewUtils
import javax.inject.Inject
import kotlin.math.ln
import kotlin.math.roundToInt
import kotlin.system.exitProcess

/**
 *
 * Class: com.oheuropa.android.data.AudioPlayer
 * Project: OhEuropa-Android
 * Created Date: 12/03/2018 14:16
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class AudioPlayer @Inject constructor(ctx: Context) : AudioComponent {

    private val staticAudio = StaticAudio(ctx)
    private val radioAudio = RadioAudio(ctx)
    private var currentState = QUIET

    override fun setState(newState: AudioComponent.State) {
        v { "setState:$newState" }
        if (currentState != newState)
            Thread {
                when (newState) {
                    QUIET -> {
                        staticAudio.fadeTo(VOL_MIN)
                        radioAudio.fadeTo(VOL_MIN)
                    }
                    STATIC -> {
                        staticAudio.fadeTo(VOL_MAX_STATIC)
                        radioAudio.fadeTo(VOL_MIN)
                    }
                    STATIC_MIX -> {
                        staticAudio.fadeTo(VOL_MAX_STATIC)
                        radioAudio.fadeTo(VOL_MIN_RADIO)
                    }
                    SIGNAL -> {
                        staticAudio.fadeTo(VOL_MIN)
                        radioAudio.fadeTo(VOL_MAX_RADIO)
                    }
                }
                currentState = newState
            }.start()
    }

    override fun activate() {
        v { "activate()" }
        setState(QUIET)
    }

    override fun deactivate() {
        v { "deactivate()" }
        staticAudio.stopAfterFade()
        radioAudio.stopAfterFade()
    }

    class RadioAudio(ctx: Context) : Audio(ctx) {
        override fun createMediaPlayer(): MediaPlayer {
            v { "creating radio player" }
            val mp = MediaPlayer.create(ctx, RADIO_STREAM_URL.toUri())
            prepared = true
            return mp
        }

        override fun name(): String {
            return "RadioAudio"//: $mediaPlayer"
        }
    }

    class StaticAudio(ctx: Context) : Audio(ctx) {
        override fun createMediaPlayer(): MediaPlayer {
            val mediaPlayer = MediaPlayer.create(ctx, R.raw.noise)
            prepared = true
            mediaPlayer.isLooping = true
            return mediaPlayer
        }

        override fun name(): String {
            return "StaticAudio"
        }
    }

    abstract class Audio constructor(val ctx: Context) {
        private var volume = 0
            set(vol) {
                val newVol = 1 - (ln(MAX_VOLUME - volume) / ln(MAX_VOLUME)).toFloat()
                try {
                    if (isPlaying())
                        mediaPlayer?.setVolume(newVol, newVol)
                } catch (e: Exception) {
                    w { "Unable to set volume, media player is not in correct currentState" }
                }
                field = vol
            }
        private var mediaPlayer: MediaPlayer? = null
        protected var prepared = false
        private var volumeAnimator: ValueAnimator? = null

        private fun createAudioPlayer(): MediaPlayer {
            if (mediaPlayer == null)
                mediaPlayer = createMediaPlayerWithListeners()
            return mediaPlayer!!
        }

        private fun createMediaPlayerWithListeners(): MediaPlayer {
            val player = createMediaPlayer()
            player.apply {
                setOnCompletionListener { d { "${name()} complete" } }
//                setOnInfoListener { _, what, extra ->
//                    d { "${name()} info: what($what) extra($extra)" }
//                    true
//                }
//                setOnErrorListener { _, what, extra ->
//                    d { "${name()} error: what($what) extra($extra)" }
//                    true
//                }
            }
            return player
        }

        abstract fun createMediaPlayer(): MediaPlayer
        abstract fun name(): String

        private fun stop() {
            d { "stopping audio..." }
            prepared = false
            try {
                volumeAnimator?.cancel()
            } catch (ex: Exception) {
                w(ex) { "Error cancelling value animator..." }
            }
            volume = 0
            mediaPlayer?.apply {
                try {
                    stop()
                } catch (ex: Throwable) {
                    w(ex) { "Failed to stop" }
                }
                try {
                    reset()
                } catch (ex: Throwable) {
                    w(ex) { "Failed to reset" }
                }
                try {
                    release()
                } catch (ex: Throwable) {
                    w(ex) { "Failed to release" }
                }
            }
            mediaPlayer = null
            d { "mediaPlayer disposed" }
        }

        fun stopAfterFade() {
            val delay =
                if (volume > 0f) {
                    fadeTo(0, EXIT_FADE_DURATION_MS)
                    (EXIT_FADE_DURATION_MS * 1.1).toLong()//wait for fade before stopping
                } else {
                    50//stop immediately(ish)
                }
            ViewUtils.handler().postDelayed({
                stop()
                exitProcess(0)//nuclear option todo: fix this with coroutines
            }, delay)
        }

        private fun setType(mediaPlayer: MediaPlayer) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
            } else {
                @Suppress("DEPRECATION")
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            }
        }

        private fun play(mediaPlayer: MediaPlayer) {
            v { "play(${name()})" }
            if (!prepared) {
                try {
                    v { "media player is stopped(playing:${isPlaying()}), preparing..." }
                    mediaPlayer.setOnPreparedListener { mp ->
                        prepared = true
                        onPrepared(mp)
                    }
                    setType(mediaPlayer)
                    mediaPlayer.prepareAsync()
                } catch (e: Exception) {
                    w { "Ignoring prepare error" }
                    onPrepared(mediaPlayer)
                }
            } else
                onPrepared(mediaPlayer)
        }

        private fun isPlaying(): Boolean {
            return try {
                mediaPlayer?.isPlaying ?: false
            } catch (e: Exception) {
                w { "Error in mediaPlayer.isPlaying($e), retuning false" }
                false
            }
        }

        private fun onPrepared(mediaPlayer: MediaPlayer) {
            try {
                v { "onPrepared(${name()})" }
                mediaPlayer.start()
                mediaPlayer.setVolume(0f, 0f)
            } catch (e: Exception) {
                w { "Error in mediaPlayer.start():$e" }
            }

        }

        fun pause() {
            if (isPlaying()) {
                v { "pausing ${name()}" }
                mediaPlayer?.pause()
            } else {
                v { "no need to pause ${name()}" }
            }
        }


        fun fadeTo(targetVolume: Int, fadeDuration: Long = FADE_DURATION_MS) {
            v { "${name()}.fadeTo($targetVolume) from $volume" }
            if (!isPlaying() && targetVolume > 0)
                play(createAudioPlayer())
            if (targetVolume != volume) {
                ViewUtils.handler().post {
                    if (volumeAnimator != null) {
                        volumeAnimator?.cancel()
                    }
                    volumeAnimator = ValueAnimator.ofFloat(volume.toFloat(), targetVolume.toFloat())
                    volumeAnimator?.duration = fadeDuration
                    volumeAnimator?.interpolator = EasingInterpolator(Ease.SINE_IN_OUT)
                    volumeAnimator?.addUpdateListener { animation ->
                        throttleVolume(animation.animatedValue as Float)
                    }
                    volumeAnimator?.addListener(object : Animator.AnimatorListener {
                        var cancelled = false
                        override fun onAnimationStart(animation: Animator?) {
                            //Timber.i("animation started on ${name()}")
                            cancelled = false
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            //Timber.i("fade finished to target:$targetVolume on ${name()} with cancelled:$cancelled")
                            if (targetVolume == 0 && !cancelled)
                                pause()
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                            //Timber.w("animation cancelled on ${name()}")
                            cancelled = true
                        }

                        override fun onAnimationRepeat(animation: Animator?) {}
                    })
                    volumeAnimator?.start()
                }
            }
        }

        private var lastUpdate: Long = 0

        private fun throttleVolume(vol: Float) {
            val now = System.currentTimeMillis()
            if (now > lastUpdate + MIN_VOLUME_INTERVAL_MS) {
                //v { "Setting ${name()} vol: $vol" }
                volume = vol.roundToInt()
                lastUpdate = now
            }
        }
    }
}
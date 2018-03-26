package com.oheuropa.android.data

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import androidx.net.toUri
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
import kotlin.math.roundToInt

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
			Thread().run {
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
			}
	}

	override fun activate() {
		v { "activate()" }
		setState(AudioComponent.State.QUIET)
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
				val newVol = 1 - (Math.log(MAX_VOLUME - volume) / Math.log(MAX_VOLUME)).toFloat()
				try {
					//if(mediaPlayer.isPlaying)
					mediaPlayer.setVolume(newVol, newVol)
				} catch (e: Exception) {
					w { "Unable to set volume, media player is not in correct currentState" }
				}
				field = vol
			}
		protected val mediaPlayer: MediaPlayer by lazy { createAudioPlayer() }
		protected var prepared = false
		private var volumeAnimator: ValueAnimator? = null

		private fun createAudioPlayer(): MediaPlayer {
			return createMediaPlayer()
			/*mediaPlayer.setOnCompletionListener { Timber.d("${name()} complete") }
			mediaPlayer.setOnInfoListener { _, what, extra ->
				Timber.d("${name()} info: what($what) extra($extra)")
				true
			}
			mediaPlayer.setOnErrorListener { _, what, extra ->
				Timber.d("${name()} error: what($what) extra($extra)")
				true
			}
			return mediaPlayer*/
		}

		abstract fun createMediaPlayer(): MediaPlayer
		abstract fun name(): String

		private fun stop() {
			d { "stopping audio..." }
			mediaPlayer.stop()
			mediaPlayer.reset()
			prepared = false
			volumeAnimator?.cancel()
			volume = 0
			mediaPlayer.release()
		}

		fun stopAfterFade() {
			if (volume > 0f) {
				fadeTo(0)
				ViewUtils.handler().postDelayed({
					stop()
				}, (FADE_DURATION_MS * 1.1).toLong())
			} else
				stop()
		}

		private fun setType(mediaPlayer: MediaPlayer) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				mediaPlayer.setAudioAttributes(AudioAttributes.Builder()
					.setUsage(AudioAttributes.USAGE_GAME)
					.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
					.build())
			} else {
				@Suppress("DEPRECATION")
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
			}
		}

		private fun play(mediaPlayer: MediaPlayer) {
			v { "play(${name()})" }
			if (!prepared) {
				try {
					v { "media player is stopped(playing:${mediaPlayer.isPlaying}), preparing..." }
					mediaPlayer.setOnPreparedListener({ mp ->
						prepared = true
						onPrepared(mp)
					})
					setType(mediaPlayer)
					mediaPlayer.prepareAsync()
				} catch (e: Exception) {
					w { "Ignoring prepare error" }
					onPrepared(mediaPlayer)
				}
			} else
				onPrepared(mediaPlayer)
		}

		private fun onPrepared(mediaPlayer: MediaPlayer) {
			v { "onPrepared(${name()})" }
			mediaPlayer.start()
			mediaPlayer.setVolume(0f, 0f)

		}

		fun pause() {
			if (mediaPlayer.isPlaying) {
				v { "pausing ${name()}" }
				mediaPlayer.pause()
			} else {
				v { "no need to pause ${name()}" }
			}
		}


		fun fadeTo(targetVolume: Int) {
			v { "${name()}.fadeTo($targetVolume) from $volume" }
			if (!mediaPlayer.isPlaying && targetVolume > 0)
				play(mediaPlayer)
			if (targetVolume != volume) {
				ViewUtils.handler().post {
					if (volumeAnimator != null) {
						volumeAnimator?.cancel()
					}
					volumeAnimator = ValueAnimator.ofFloat(volume.toFloat(), targetVolume.toFloat())
					volumeAnimator?.duration = FADE_DURATION_MS
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
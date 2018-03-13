package com.oheuropa.android.data

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.net.toUri
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.oheuropa.android.R
import com.oheuropa.android.domain.AudioComponent
import timber.log.Timber
import javax.inject.Inject

/**
 *
 * Class: com.oheuropa.android.data.AudioPlayer
 * Project: OhEuropa-Android
 * Created Date: 12/03/2018 14:16
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
const val FADE_DURATION_MS = 5000.toLong()
const val RADIO_STREAM_URL = "https://streams.radio.co/s02776f249/listen"
const val MAX_VOLUME = 100.toDouble()
const val MIN_VOLUME_INTERVAL_MS = 250

class AudioPlayer @Inject constructor(ctx: Context) : AudioComponent {

	private val staticAudio = StaticAudio(ctx)
	private val radioAudio = RadioAudio(ctx)

	override fun setStreamUrl(radioStreamUrl: String) {
		radioAudio.setStreamUrl(radioStreamUrl)
	}

	override fun setState(state: AudioComponent.State) {
		Timber.v("setState:%s", state)
		Thread().run {
			when (state) {
				AudioComponent.State.QUIET -> {
					staticAudio.fadeTo(0f)
					radioAudio.fadeTo(0f)
				}
				AudioComponent.State.STATIC -> {
					staticAudio.fadeTo(75f)
					radioAudio.fadeTo(0f)
				}
				AudioComponent.State.STATIC_MIX -> {
					staticAudio.fadeTo(75f)
					radioAudio.fadeTo(15f)
				}
				AudioComponent.State.SIGNAL -> {
					staticAudio.fadeTo(0f)
					radioAudio.fadeTo(75f)
				}
			}
		}
	}

	override fun activate() {
		Timber.v("activate()")
		setState(AudioComponent.State.QUIET)
	}

	override fun deactivate() {
		Timber.v("deactivate()")
		staticAudio.stop()
		radioAudio.stop()
	}

	class RadioAudio(ctx: Context) : Audio(ctx) {
		override fun createMediaPlayer(): MediaPlayer {
			return MediaPlayer()
		}

		private var streamUrl: Uri = RADIO_STREAM_URL.toUri()

		fun setStreamUrl(radioStreamUrl: String) {
			Timber.v("setStreamUrl:%s", radioStreamUrl)
			streamUrl = radioStreamUrl.toUri()
			mediaPlayer.setDataSource(ctx, streamUrl)
			mediaPlayer.prepare()
			prepared = true
		}

		override fun name(): String {
			return "RadioAudio"
		}
	}

	class StaticAudio(ctx: Context) : Audio(ctx) {
		override fun createMediaPlayer(): MediaPlayer {
			val mediaPlayer = MediaPlayer.create(ctx, R.raw.noise)
			mediaPlayer.isLooping = true
			return mediaPlayer
		}

		override fun name(): String {
			return "StaticAudio"
		}
	}

	abstract class Audio constructor(val ctx: Context) {
		private var volume = 0f
			set(vol) {
				val newVol = 1 - (Math.log(MAX_VOLUME - volume) / Math.log(MAX_VOLUME)).toFloat()
				mediaPlayer.setVolume(newVol, newVol)
				field = vol
			}
		protected val mediaPlayer: MediaPlayer by lazy { createAudioPlayer() }
		protected var prepared = false
		private var volumeAnimator: ValueAnimator? = null

		private fun createAudioPlayer(): MediaPlayer {
			val mediaPlayer = createMediaPlayer()
			prepared = true
			setType(mediaPlayer)
			/*mediaPlayer.setOnCompletionListener { Timber.d("${name()} complete") }
			mediaPlayer.setOnInfoListener { _, what, extra ->
				Timber.d("${name()} info: what($what) extra($extra)")
				true
			}
			mediaPlayer.setOnErrorListener { _, what, extra ->
				Timber.d("${name()} error: what($what) extra($extra)")
				true
			}*/
			return mediaPlayer
		}

		abstract fun createMediaPlayer(): MediaPlayer
		abstract fun name(): String

		fun stop() {
			mediaPlayer.stop()
			prepared = false
			volumeAnimator?.cancel()
			volume = 0f
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
			Timber.v("play(${name()})")
			if (!prepared) {
				try {
					Timber.d("media player is stopped, preparing...")
					mediaPlayer.prepare()
					prepared = true
				} catch (e: Exception) {
					Timber.e("Ignoring prepare error")
				}
			}
			mediaPlayer.start()
			mediaPlayer.setVolume(0f, 0f)
		}

		fun pause() {
			if (mediaPlayer.isPlaying) {
				Timber.v("pausing ${name()}")
				mediaPlayer.pause()
			} else {
				Timber.v("no need to pause ${name()}")
			}
		}


		fun fadeTo(targetVolume: Float) {
			Timber.v("${name()}.fadeTo($targetVolume) from $volume")
			if (!mediaPlayer.isPlaying && targetVolume > 0)
				play(mediaPlayer)
			if (targetVolume != volume) {
				if (volumeAnimator != null) {
					volumeAnimator?.cancel()
				}
				volumeAnimator = ValueAnimator.ofFloat(volume, targetVolume)
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
						if (targetVolume == 0f && !cancelled)
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

		private var lastUpdate: Long = 0

		private fun throttleVolume(vol: Float) {
			val now = System.currentTimeMillis()
			if (now > lastUpdate + MIN_VOLUME_INTERVAL_MS) {
				//Timber.d("Setting ${name()} vol: $vol")
				volume = vol
				lastUpdate = now
			}
		}
	}
}
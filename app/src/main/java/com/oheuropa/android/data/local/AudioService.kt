package com.oheuropa.android.data.local

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.e
import com.github.ajalt.timberkt.i
import com.github.ajalt.timberkt.v
import com.oheuropa.android.domain.AudioComponent
import com.oheuropa.android.domain.AudioComponent.State.*
import com.oheuropa.android.domain.BeaconWatcher
import com.oheuropa.android.model.BeaconLocation
import com.oheuropa.android.model.BeaconLocation.CircleState.*
import com.oheuropa.android.ui.base.SchedulersFacade
import dagger.android.AndroidInjection
import io.reactivex.disposables.Disposable
import javax.inject.Inject


/**
 *
 * Class: com.oheuropa.android.data.local.AudioService
 * Project: OhEuropa-Android
 * Created Date: 20/03/2018 07:04
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class  AudioService : Service() {

	@Inject lateinit var audioComponent: AudioComponent
	@Inject lateinit var beaconWatcher: BeaconWatcher

	companion object {
		var boundActivities = 0

		fun createIntent(ctx: Context): Intent {
			return Intent(ctx, AudioService::class.java)
		}

		fun bindService(activity: Activity): AudioConnection {
			val i = createIntent(activity)
			val connection = AudioConnection()
			activity.bindService(i, connection, Context.BIND_AUTO_CREATE)
			return connection
		}

		fun unbindService(activity: Activity, connection: ServiceConnection) {
			activity.unbindService(connection)
		}

		class AudioConnection : ServiceConnection {
			override fun onServiceConnected(name: ComponentName, binder: IBinder) {
				v { "onServiceConnected($name, $binder)" }
				boundActivities++
			}

			override fun onServiceDisconnected(name: ComponentName?) {
				v { "onServiceDisconnected($name)" }
				boundActivities--
			}

			fun unbind(activity: Activity) {
				unbindService(activity, this)
			}
		}
	}

	private var beaconListener: Disposable? = null
	private val binder = AudioBinder()

	class AudioBinder : Binder()

	override fun onCreate() {
		AndroidInjection.inject(this)
		super.onCreate()
		startListening()
	}

	override fun onBind(intent: Intent?): IBinder {
		v { "onBind: $intent" }
		return binder
	}


	private fun startListening() {
		audioComponent.activate()
		beaconListener = beaconWatcher.followBeaconLocation()
			.subscribeOn(SchedulersFacade.io())
			.observeOn(SchedulersFacade.io())
			.subscribe({
				reactTo(it)
			}, {
				e(it) { "Error in beacon watcher" }
			})
	}


	private fun reactTo(beaconLocation: BeaconLocation) {
		v { "got new BeaconLocation: $beaconLocation" }
		when (beaconLocation.getCircleState()) {
			CENTRE -> audioComponent.setState(SIGNAL)
			INNER -> audioComponent.setState(STATIC_MIX)
			OUTER -> audioComponent.setState(STATIC)
			NONE -> {
				audioComponent.setState(QUIET)
				if (boundActivities == 0) {
					v { "out of range, and no bound activities, so stop listening" }
					stopSelf()
				} else {
					v { "out of range, but we have $boundActivities bound activities, so keep listening" }
				}
			}
		}
	}

	override fun onDestroy() {
		v { "service destroyed" }
		audioComponent.deactivate()
		beaconListener?.dispose()
		super.onDestroy()
	}
}
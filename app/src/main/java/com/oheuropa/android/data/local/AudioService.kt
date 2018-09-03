package com.oheuropa.android.data.local

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import com.github.ajalt.timberkt.*
import com.oheuropa.android.data.DataManager
import com.oheuropa.android.domain.AudioComponent
import com.oheuropa.android.domain.AudioComponent.State.*
import com.oheuropa.android.domain.BeaconWatcher
import com.oheuropa.android.model.BeaconLocation
import com.oheuropa.android.model.BeaconLocation.CircleState.*
import com.oheuropa.android.model.UserRequest
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
class AudioService : Service() {

	@Inject lateinit var audioComponent: AudioComponent
	@Inject lateinit var beaconWatcher: BeaconWatcher
	@Inject lateinit var dataManager: DataManager

	private var currentState: BeaconLocation.CircleState = NONE

	companion object {
		var boundActivities = 0

		fun createIntent(ctx: Context): Intent {
			return Intent(ctx, AudioService::class.java)
		}

		fun bindService(activity: Activity): AudioConnection {
			i { "bindService($activity), boundActivities:$boundActivities" }
			val i = createIntent(activity)
			val connection = AudioConnection()
			activity.bindService(i, connection, Context.BIND_AUTO_CREATE)
			boundActivities++
			return connection
		}

		fun unbindService(activity: Activity, connection: ServiceConnection) {
			i { "unbindService($activity)" }
			try {
				if(boundActivities > 0) {
					activity.unbindService(connection)
					boundActivities--
				}
			} catch(e: Exception) {
				w(e){"Unable to unbind service, boundActivities:$boundActivities"}
			}
		}

		class AudioConnection : ServiceConnection {
			override fun onServiceConnected(name: ComponentName, binder: IBinder) {
				v { "onServiceConnected($name, $binder)" }
			}

			override fun onServiceDisconnected(name: ComponentName?) {
				v { "onServiceDisconnected($name)" }
			}

			fun unbind(activity: Activity) {
				v { "onServiceUnbound($activity)" }
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
				v { "got new BeaconLocation: $it" }
				reactAudio(it)
				reactAnalytics(it)
			}, {
				e(it) { "Error in beacon watcher" }
			})
	}


	private fun reactAudio(beaconLocation: BeaconLocation) {
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
					v { "out of range, but $boundActivities bound activities, so keep listening" }
				}
			}
		}
	}

	private fun reactAnalytics(beaconLocation: BeaconLocation) {
		val newState = beaconLocation.getCircleState()
		if (newState != currentState) {
			d { "recording state change from $currentState to $newState" }
			val id = beaconLocation.getPlaceId()
			if (currentState != NONE) {
				//record exit change
				dataManager.uploadUserInteraction(id, currentState, UserRequest.Action.Exited)
			}
			if (newState != NONE) {
				//record enter change
				dataManager.uploadUserInteraction(id, newState, UserRequest.Action.Entered)
			}
		}
		currentState = newState
	}

	override fun onDestroy() {
		v { "service destroyed" }
		audioComponent.deactivate()
		beaconListener?.dispose()
		super.onDestroy()
	}
}
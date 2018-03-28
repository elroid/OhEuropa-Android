package com.oheuropa.android.ui.compass

import com.github.ajalt.timberkt.e
import com.github.ajalt.timberkt.v
import com.oheuropa.android.data.local.AnalyticsHelper
import com.oheuropa.android.data.remote.OhEuropaApiService
import com.oheuropa.android.domain.BeaconWatcher
import com.oheuropa.android.domain.CompassComponent
import com.oheuropa.android.domain.LocationComponent
import com.oheuropa.android.domain.TRACK_NAME_UPDATE_DELAY_MS
import com.oheuropa.android.model.BeaconLocation
import com.oheuropa.android.model.BeaconLocation.CircleState.CENTRE
import com.oheuropa.android.ui.base.LocationEnabledPresenter
import com.oheuropa.android.ui.base.SchedulersFacade
import com.oheuropa.android.util.GenUtils.Companion.limit360
import com.oheuropa.android.util.GenUtils.Companion.printCallingMethod
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlin.math.roundToInt

/**
 *
 * Class: com.oheuropa.android.ui.compass.CompassPresenter
 * Project: OhEuropa-Android
 * Created Date: 15/03/2018 19:12
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class CompassPresenter(
	view: CompassContract.View,
	locator: LocationComponent,
	private val beaconWatcher: BeaconWatcher,
	private val compass: CompassComponent,
	private val apiService: OhEuropaApiService
) : LocationEnabledPresenter<CompassContract.View>(view, locator), CompassContract.Presenter {

	private var beaconLocation: BeaconLocation? = null
	private var songTitle: String = ""
	private var performerName: String = ""

	private fun getTrackName() {
		v { "geTrackName - called by: ${printCallingMethod()}" }
		addDisposable(apiService.getAudioStatus()
			.subscribeOn(SchedulersFacade.io())
			.observeOn(SchedulersFacade.io())
			.subscribe({
				performerName = it.current_track.getPerformerName()
				songTitle = it.current_track.getSongTitle()
				v { "got new song title($songTitle) and performer($performerName)" }
				if (beaconLocation?.getCircleState() == BeaconLocation.CircleState.CENTRE) {
					view.showSongInfo(performerName, songTitle)
				}
			}, {
				e(it) { "error getting audio status" }
			}))
	}

	private var namePollDisposable: Disposable? = null
	private fun startActiveNamePolling() {
		if (namePollDisposable != null) return
		v { "startActiveNamePolling()" }
		val result: Observable<Void> = Observable.create({
			while (!it.isDisposed) {
				getTrackName()
				try {
					Thread.sleep(TRACK_NAME_UPDATE_DELAY_MS)
				} catch (ignored: Exception) {
				}
			}
		})
		namePollDisposable = addDisposable(result
			.subscribeOn(SchedulersFacade.io())
			.observeOn(SchedulersFacade.io())
			.subscribe()
		)
	}

	private fun stopActiveNamePolling() {
		if (namePollDisposable != null) {
			v { "stopActiveNamePolling, disposable:$namePollDisposable" }
			namePollDisposable?.dispose()
			namePollDisposable = null
		}
	}

	private fun checkStatus() {
		if (beaconLocation?.getCircleState() == CENTRE) {
			startActiveNamePolling()
		}
	}

	override fun onConnected() {
		super.onConnected()

		checkStatus()

		//watch beacons and keep beaconLocation var updated
		addDisposable(beaconWatcher.followBeaconLocation()
			.subscribeOn(SchedulersFacade.io())
			.observeOn(SchedulersFacade.io())
			.subscribe({
				v { "got new BeaconLocation: $it" }
				beaconLocation = it
				checkStatus()
			}, {
				e { "error in beaconWatcher!" }
				AnalyticsHelper.logException(it, "beaconWatcher.error")
			})
		)

		//listen to compass and update UI
		addDisposable(compass.listenToCompass()
			.subscribeOn(SchedulersFacade.io())
			.observeOn(SchedulersFacade.io())
			.subscribe({
				//i { "Compass reading: $it" }
				if (beaconLocation != null) {
					val (distance, bearing) = beaconLocation!!.getDistanceAndBearing()
					val circleState = beaconLocation!!.getCircleState()
					//i { "circleState:$circleState" }
					when (circleState) {
						CENTRE -> {
							//show song info and deactivate compass
							startActiveNamePolling()
						}
						else -> {
							//show distance and activate compass
							val northBearing = it
							val beaconBearing = limit360(it - bearing)
							val dist = distance.roundToInt()
							view.showNewReading(0 - northBearing, 0 - beaconBearing, dist)
							stopActiveNamePolling()
						}
					}
				}
			}, {
				view.showError(msg = it.message)
				AnalyticsHelper.logException(it, "listenToCompass.error")
			}))
	}
}
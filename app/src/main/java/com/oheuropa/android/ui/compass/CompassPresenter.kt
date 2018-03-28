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
import com.oheuropa.android.util.GenUtils
import com.oheuropa.android.util.GenUtils.Companion.limit360
import com.oheuropa.android.util.GenUtils.Companion.printCallingMethod
import com.oheuropa.android.util.PairFunction
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

	private var songTitle: String = ""
	private var performerName: String = ""
	private var isInCentre = false

	private fun getTrackName() {
		v { "geTrackName - called by: ${printCallingMethod()}" }
		addDisposable(apiService.getAudioStatus()
			.subscribeOn(SchedulersFacade.io())
			.observeOn(SchedulersFacade.io())
			.subscribe({
				performerName = it.current_track.getPerformerName()
				songTitle = it.current_track.getSongTitle()
				v { "got new song title($songTitle) and performer($performerName)" }
				if (isInCentre) {
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
		if (isInCentre) {
			startActiveNamePolling()
		}
	}

	override fun onConnected() {
		super.onConnected()
		v { "onConnected thread:${GenUtils.printThread()}" }
		checkStatus()

		val beaconObservable = beaconWatcher.followBeaconLocation()
		val compassFlowable = compass.listenToCompass()

		val beaconCompassObservable = Observable
			.combineLatest<BeaconLocation, Float, Pair<BeaconLocation, Float>>(beaconObservable,
				compassFlowable.toObservable(), PairFunction())

		addDisposable(beaconCompassObservable
			.subscribeOn(SchedulersFacade.io())
			.observeOn(SchedulersFacade.io())
			.subscribe({
				val beaconLocation = it.first
				val compassReading = it.second
				v { "combined:$it thread:${GenUtils.printThread()}" }
				val (distance, bearing) = beaconLocation.getDistanceAndBearing()
				val circleState = beaconLocation.getCircleState()
				when (circleState) {
					CENTRE -> {
						//show song info and deactivate compass
						startActiveNamePolling()
						isInCentre = true
					}
					else -> {
						isInCentre = false
						//show distance and activate compass
						val beaconBearing = limit360(compassReading - bearing)
						val dist = distance.roundToInt()
						view.showNewReading(0 - compassReading,
							0 - beaconBearing, dist)
						stopActiveNamePolling()
					}
				}
			}, {
				view.showError(msg = it.message)
				AnalyticsHelper.logException(it, "beaconCompassObservable.error")
			}
			))
	}
}
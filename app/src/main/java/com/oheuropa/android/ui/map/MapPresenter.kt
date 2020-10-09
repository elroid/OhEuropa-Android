package com.oheuropa.android.ui.map

import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.w
import com.github.ajalt.timberkt.wtf
import com.oheuropa.android.data.local.PrefsHelper
import com.oheuropa.android.domain.BeaconWatcher
import com.oheuropa.android.domain.DEFAULT_MAP_ZOOM
import com.oheuropa.android.domain.LocationComponent
import com.oheuropa.android.domain.MAP_ZOOM_DURATION_SECONDS
import com.oheuropa.android.model.BeaconLocation
import com.oheuropa.android.model.Coordinate
import com.oheuropa.android.ui.base.LocationEnabledPresenter
import com.oheuropa.android.ui.base.SchedulersFacade

/**
 *
 * Class: com.oheuropa.android.ui.map.MapPresenter
 * Project: OhEuropa-Android
 * Created Date: 14/03/2018 13:04
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class MapPresenter(
	mapView: MapContract.View,
	locator: LocationComponent,
	private val beaconWatcher: BeaconWatcher,
	private val prefs: PrefsHelper
):LocationEnabledPresenter<MapContract.View>(mapView, locator), MapContract.Presenter {

	//default europe centre
	private val europeCentre = Coordinate(56.05500303882426, 11.342917084693907)
	private val europeZoom = 3.3f

	enum class MapState {
		FOCUS_HERE, FOCUS_EUROPE, FOCUS_OTHER
	}

	private var markersInitialised = false
	private var zoomInitialised = false

	private var currentZoom: Float = DEFAULT_MAP_ZOOM
	private var currentCentre: Coordinate? = null
	private var currentState: MapState? = null

	private var beaconLocation: BeaconLocation? = null

	override fun onConnected() {
		d { "MapPresenter.onConnected" }
		super.onConnected()
		setupMap()
	}

	private fun setupMap() {
		markersInitialised = false
		zoomInitialised = false

		//if we have a map state in memory - restore that
		val (centre, zoom, state) = prefs.restoreMapCentre()
		if(centre.isValid()) {
			d { "map-init: restoring saved centre($centre) and zoom($zoom)" }
			view.zoomTo(centre, zoom, 0)
			currentState = state
			// if it's HERE we want to show most recent location
			zoomInitialised = state != MapState.FOCUS_HERE
		} else {
			//...otherwise show all of europe
			zoomToEuropeLevel(0)
		}

		addDisposable(beaconWatcher.followBeaconLocation()
			.subscribeOn(SchedulersFacade.io())
			.observeOn(SchedulersFacade.ui())
			.subscribe({
				beaconLocation = it
				view.showMyLocation(it.myLocation)
				if(!markersInitialised) {
					view.showBeacons(it.beacons)
					markersInitialised = true
				}
				if(!zoomInitialised) {
					zoomToNearest()
					zoomInitialised = true
				}
			}, {
				view.showError(msg = it.message)
				wtf(it) { "beaconWatcher-map.error" }
			}))
	}

	override fun onCameraIdle(centre: Coordinate, zoom: Float) {
		currentZoom = zoom
		currentCentre = centre
		d { "map-init: recording idle zoom($currentZoom) and pos($currentCentre)" }
	}

	override fun saveMapState() {
		prefs.saveMapCentre(currentCentre, currentZoom, currentState)
	}

	private fun zoomToEuropeLevel(durationSeconds: Int = MAP_ZOOM_DURATION_SECONDS) {
		//show all beacons (if available) or default europe

		/*if (beaconLocation == null) {
			d { "map-init: showing default europe centre" }
			view.zoomTo(europeCentre, europeZoom, durationSeconds)
		}
		else {
			d { "map-init: showing all beacons" }
			beaconLocation?.apply {
				view.zoomTo(beacons, myLocation)
			}
		}*/
		view.zoomTo(europeCentre, europeZoom, durationSeconds)
		currentState = MapState.FOCUS_EUROPE
	}

	private fun zoomToNearest() {
		//zoom to my location and my closest beacon (if available)
		beaconLocation?.apply {
			view.zoomTo(listOf(beacons[0]), myLocation)
			currentState = MapState.FOCUS_HERE
		}
	}

	override fun onMapWander() {
		currentState = MapState.FOCUS_OTHER
	}

	override fun onMapTabPressed() {
		d { "onMapTabPressed, current;$currentState" }
		when(currentState) {
			MapState.FOCUS_HERE -> zoomToEuropeLevel()
			MapState.FOCUS_EUROPE -> zoomToNearest()
			MapState.FOCUS_OTHER -> zoomToNearest()
		}
	}

	override fun onError(ex: Exception) {
		w(ex) { "Swallowing map error and just showing europe" }
		setupMap()
	}
}
package com.oheuropa.android.ui.map

import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.e
import com.github.ajalt.timberkt.w
import com.google.android.gms.common.api.ResolvableApiException
import com.oheuropa.android.domain.BeaconWatcher
import com.oheuropa.android.domain.LocationComponent
import com.oheuropa.android.ui.base.BasePresenter
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
	private val beaconWatcher: BeaconWatcher,
	private val locator: LocationComponent
) : BasePresenter<MapContract.View>(mapView), MapContract.Presenter, LocationComponent.LocationStartListener {

	override fun start() {
		d { "MapPresenter.start" }
		locator.start(this)

	}

	override fun onSuccess() {
		d { "MapPresenter.onSuccess" }
		addDisposable(beaconWatcher.followBeaconLocation(locator)
			.subscribeOn(SchedulersFacade.ui())//do it on ui so the location listener gets called on correct thread
			.observeOn(SchedulersFacade.ui())
			.subscribe({
				view.showBeacons(it.beacons)
				view.showMyLocation(it.myLocation)
				view.zoomTo(it.beacons, it.myLocation)
			}, {
				e(it)
				view.showError(msg = it.message)
			}))
	}

	override fun onPermissionsError(ex: SecurityException) {
		w(ex){"onPermissionsError($ex)"}

	}

	override fun onApiError(ex: ResolvableApiException) {
		view.resolveApiIssue(ex)
	}

	override fun onError(ex: Exception) {
		e(ex) { "General error from LocationComponent" }
		view.showError(msg = ex.message)
	}
}
package com.oheuropa.android.ui.map

import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.e
import com.oheuropa.android.domain.BeaconWatcher
import com.oheuropa.android.domain.LocationComponent
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
	private val beaconWatcher: BeaconWatcher
) : LocationEnabledPresenter<MapContract.View>(mapView, locator), MapContract.Presenter {

	override fun onConnected() {
		d { "MapPresenter.onConnected" }
		addDisposable(beaconWatcher.followBeaconLocation(locator)
			.subscribeOn(SchedulersFacade.ui())//do it on ui - location listener complains otherwise
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
}
package com.oheuropa.android.ui.map

import com.github.ajalt.timberkt.e
import com.oheuropa.android.domain.BeaconWatcher
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
	private val beaconWatcher: BeaconWatcher
) : BasePresenter<MapContract.View>(mapView), MapContract.Presenter {

	override fun startBeaconListener() {
		addDisposable(beaconWatcher.followBeaconLocation()
			.subscribeOn(SchedulersFacade.io())
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
package com.oheuropa.android.ui.compass

import com.github.ajalt.timberkt.i
import com.oheuropa.android.domain.BeaconWatcher
import com.oheuropa.android.domain.CompassComponent
import com.oheuropa.android.domain.LocationComponent
import com.oheuropa.android.ui.base.LocationEnabledPresenter

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
	beaconWatcher: BeaconWatcher,
	private val compass: CompassComponent
) : LocationEnabledPresenter<CompassContract.View>(view, locator), CompassContract.Presenter {

	override fun onConnected() {
		addDisposable(compass.listenToCompass()
			.subscribe({
				i { "Compass reading: $it" }
				//todo drive compass ui
			}, {
				view.showError(msg = it.message)
			}))
	}
}
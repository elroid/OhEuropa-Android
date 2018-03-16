package com.oheuropa.android.ui.compass

import com.oheuropa.android.domain.BeaconWatcher
import com.oheuropa.android.domain.CompassComponent
import com.oheuropa.android.domain.LocationComponent
import dagger.Module
import dagger.Provides

/**
 *
 * Class: com.oheuropa.android.ui.map.MapModule
 * Project: OhEuropa-Android
 * Created Date: 14/03/2018 12:52
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@Module
class CompassModule {

	@Provides
	fun provideCompassPresenter(mapView: CompassContract.View,
								locator: LocationComponent,
								beaconWatcher: BeaconWatcher,
								compass: CompassComponent): CompassContract.Presenter {
		return CompassPresenter(mapView, locator, beaconWatcher, compass)
	}
}
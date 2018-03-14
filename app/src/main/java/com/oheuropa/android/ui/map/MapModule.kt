package com.oheuropa.android.ui.map

import com.oheuropa.android.domain.BeaconWatcher
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
class MapModule {

	@Provides
	fun provideMapPresenter(mapView: MapContract.View,
							beaconWatcher: BeaconWatcher): MapContract.Presenter {
		return MapPresenter(mapView, beaconWatcher)
	}
}
package com.oheuropa.android.ui.map

import dagger.Binds
import dagger.Module

/**
 *
 * Class: com.oheuropa.android.ui.map.MapViewModule
 * Project: OhEuropa-Android
 * Created Date: 14/03/2018 13:06
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@Module
abstract class MapViewModule {

	@Binds
	abstract fun provideMapView(mapActivity:MapActivity):MapContract.View
}
package com.oheuropa.android.ui.compass

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
@Suppress("unused")
@Module
abstract class CompassViewModule {

	@Binds
	abstract fun provideCompassView(mapActivity: CompassActivity): CompassContract.View
}
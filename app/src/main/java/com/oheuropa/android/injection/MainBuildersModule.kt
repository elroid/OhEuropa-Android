package com.oheuropa.android.injection

import com.oheuropa.android.data.local.AudioService
import com.oheuropa.android.ui.compass.CompassActivity
import com.oheuropa.android.ui.compass.CompassModule
import com.oheuropa.android.ui.compass.CompassViewModule
import com.oheuropa.android.ui.map.MapActivity
import com.oheuropa.android.ui.map.MapModule
import com.oheuropa.android.ui.map.MapViewModule
import com.oheuropa.android.ui.start.StartActivity
import com.oheuropa.android.ui.start.StartModule
import com.oheuropa.android.ui.start.StartViewModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 *
 * Class: com.oheuropa.android.injection.BuildersModule
 * Project: OhEuropa-Android
 * Created Date: 06/03/2018 13:19
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@Suppress("unused")
@Module
abstract class MainBuildersModule {

	@ContributesAndroidInjector(modules = [StartViewModule::class, StartModule::class])
	abstract fun bindStartActivity(): StartActivity

	@ContributesAndroidInjector(modules = [CompassViewModule::class, CompassModule::class])
	abstract fun bindCompassActivity(): CompassActivity

	@ContributesAndroidInjector(modules = [MapViewModule::class, MapModule::class])
	internal abstract fun bindMapActivity(): MapActivity

	@ContributesAndroidInjector
	abstract fun bindAudioService(): AudioService
}
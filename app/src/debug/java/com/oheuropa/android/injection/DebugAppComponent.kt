package com.oheuropa.android.injection

import com.oheuropa.android.DebugApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 *
 * Class: com.oheuropa.android.injection.DebugAppComponent
 * Project: OhEuropa-Android
 * Created Date: 09/03/2018 09:28
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AppModule::class, DebugAppModule::class, DebugBuildersModule::class])
interface DebugAppComponent : MainComponent{
	@Component.Builder
	interface Builder {
		@BindsInstance
		fun application(application: DebugApp): Builder

		fun build(): DebugAppComponent
	}

	fun inject(app: DebugApp)
}
package com.oheuropa.android.injection

import com.oheuropa.android.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 *
 * Class: AppComponent
 * Project: OhEuropa-Android
 * Created Date: 06/03/2018 13:17
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@Singleton
@Component(modules = arrayOf(
	AndroidSupportInjectionModule::class, AppModule::class, ReleaseAppModule::class, MainBuildersModule::class))
interface AppComponent : MainComponent{
	@Component.Builder
	interface Builder {
		@BindsInstance
		fun application(application: App): Builder

		fun build(): AppComponent
	}

	fun inject(app: App)

}
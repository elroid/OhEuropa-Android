package com.oheuropa.android

import android.app.Activity
import com.github.ajalt.timberkt.Timber
import android.app.Application
import com.oheuropa.android.injection.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 *
 * Class: App
 * Project: OhEuropa-Android
 * Created Date: 05/03/2018 15:18
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class App : BaseApp(), HasActivityInjector, HasServiceInjector {

	@Inject lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>
	@Inject lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

	override fun onCreate() {
		super.onCreate()

		DaggerAppComponent
			.builder()
			.application(this)
			.build()
			.inject(this)

		//todo create crashlytics debugtree before release
		Timber.plant(Timber.DebugTree())
	}

	override fun activityInjector(): AndroidInjector<Activity> {
		return dispatchingActivityInjector
	}

	override fun serviceInjector(): AndroidInjector<Service> {
		return dispatchingServiceInjector
	}
}
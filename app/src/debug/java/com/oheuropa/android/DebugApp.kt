package com.oheuropa.android

import android.app.Activity
import com.github.ajalt.timberkt.Timber
import com.oheuropa.android.injection.DaggerDebugAppComponent
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
class DebugApp : BaseApp(), HasActivityInjector {

	@Inject
	lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

	override fun onCreate() {
		super.onCreate()

		DaggerDebugAppComponent
			.builder()
			.application(this)
			.build()
			.inject(this)

		Timber.plant(Timber.DebugTree())
	}

	override fun activityInjector(): AndroidInjector<Activity> {
		return dispatchingActivityInjector
	}
}
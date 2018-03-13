package com.oheuropa.android

import android.app.Activity
import android.app.Application
import com.oheuropa.android.injection.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
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
class App : BaseApp(), HasActivityInjector {

	@Inject
	lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

	override fun onCreate() {
		super.onCreate()

		DaggerAppComponent
			.builder()
			.application(this)
			.build()
			.inject(this)
	}

	override fun activityInjector(): AndroidInjector<Activity> {
		return dispatchingActivityInjector
	}
}
package com.oheuropa.android

import android.app.Activity
import android.app.Service
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.evernote.android.job.JobManager
import com.github.ajalt.timberkt.Timber
import com.oheuropa.android.injection.DaggerAppComponent
import com.oheuropa.android.util.CrashlyticsTree
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import io.fabric.sdk.android.Fabric
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

	@Suppress("unused")//inject/initialise here so it will re-schedule after reboot
	@Inject lateinit var jobManager: JobManager

	override fun onCreate() {
		super.onCreate()

		DaggerAppComponent
			.builder()
			.application(this)
			.build()
			.inject(this)

		Timber.plant(CrashlyticsTree(Log.INFO))
		Timber.plant(Timber.DebugTree())//todo remove before release

		Fabric.with(Fabric.Builder(this).kits(Crashlytics(), Answers()).build())
	}

	override fun activityInjector(): AndroidInjector<Activity> {
		return dispatchingActivityInjector
	}

	override fun serviceInjector(): AndroidInjector<Service> {
		return dispatchingServiceInjector
	}
}
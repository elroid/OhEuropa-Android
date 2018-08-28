package com.oheuropa.android

import android.app.Activity
import android.app.Service
import android.util.Log.VERBOSE
import com.evernote.android.job.JobManager
import com.github.ajalt.timberkt.Timber
import com.oheuropa.android.injection.DaggerDebugAppComponent
import com.oheuropa.android.util.ThreadTree
import com.tspoon.traceur.Traceur
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
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
class DebugApp : BaseApp(), HasActivityInjector, HasServiceInjector {

	@Inject lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>
	@Inject lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

	@Suppress("unused")//inject here so will re-schedule after reboot
	@Inject lateinit var jobManager: JobManager

	override fun onCreate() {
		super.onCreate()

		DaggerDebugAppComponent
			.builder()
			.application(this)
			.build()
			.inject(this)

		Traceur.enableLogging()
		//Timber.plant(Timber.DebugTree())
		Timber.plant(ThreadTree(VERBOSE))
	}

	override fun activityInjector(): AndroidInjector<Activity> {
		return dispatchingActivityInjector
	}

	override fun serviceInjector(): AndroidInjector<Service> {
		return dispatchingServiceInjector
	}
}
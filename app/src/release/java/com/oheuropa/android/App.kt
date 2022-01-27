package com.oheuropa.android

import android.util.Log
import com.evernote.android.job.JobManager
import com.github.ajalt.timberkt.Timber
//import com.halfhp.rxtracer.RxTracer
import com.oheuropa.android.injection.DaggerAppComponent
import com.oheuropa.android.util.CrashlyticsTree
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
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
class App:BaseApp(), HasAndroidInjector {

	@Inject lateinit var androidInjector: DispatchingAndroidInjector<Any>

	@Suppress("unused")//inject/initialise here so it will re-schedule after reboot
	@Inject lateinit var jobManager: JobManager

	override fun onCreate() {
		super.onCreate()

		DaggerAppComponent
			.builder()
			.application(this)
			.build()
			.inject(this)

		//RxTracer.enable()
		Timber.plant(CrashlyticsTree(Log.INFO))
		//Timber.plant(Timber.DebugTree())//remove before release

		initCrashlytics(true)
	}

	override fun androidInjector(): AndroidInjector<Any> = androidInjector
}
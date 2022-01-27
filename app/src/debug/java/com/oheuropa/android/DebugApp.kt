package com.oheuropa.android

import android.content.Context
import android.util.Log.VERBOSE
import androidx.multidex.MultiDex
import com.evernote.android.job.JobManager
import com.github.ajalt.timberkt.Timber
//import com.halfhp.rxtracer.RxTracer
import com.oheuropa.android.injection.DaggerDebugAppComponent
import com.oheuropa.android.util.ThreadTree
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
class DebugApp : BaseApp(), HasAndroidInjector {

	@Inject lateinit var androidInjector: DispatchingAndroidInjector<Any>

	@Suppress("unused")//inject here so will re-schedule after reboot
	@Inject lateinit var jobManager: JobManager

	override fun onCreate() {
		super.onCreate()

		DaggerDebugAppComponent
			.builder()
			.application(this)
			.build()
			.inject(this)

		//RxTracer.enable()
		//Timber.plant(Timber.DebugTree())
		Timber.plant(ThreadTree(VERBOSE))
		initCrashlytics(false)
	}

	override fun androidInjector(): AndroidInjector<Any> = androidInjector

	override fun attachBaseContext(base: Context) {
		super.attachBaseContext(base)
		MultiDex.install(this)
	}
}
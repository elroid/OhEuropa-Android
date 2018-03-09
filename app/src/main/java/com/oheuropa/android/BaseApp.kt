package com.oheuropa.android

import android.app.Application
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

open class BaseApp @Inject constructor(): Application() {

	override fun onCreate() {
		super.onCreate()

		Timber.plant(Timber.DebugTree())
	}
}
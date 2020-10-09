package com.oheuropa.android

import android.app.Application
import android.util.Log
import com.oheuropa.android.util.CrashlyticsTree
import com.oheuropa.android.util.CrashlyticsTree.Companion.withCrashlytics
import timber.log.Timber

/**
 *
 * Class: App
 * Project: OhEuropa-Android
 * Created Date: 05/03/2018 15:18
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */

abstract class BaseApp : Application(){

	fun initCrashlytics(enabled: Boolean, logLevel: Int = Log.DEBUG) {
		withCrashlytics {
			setCrashlyticsCollectionEnabled(enabled)
		}
		if (enabled)
			Timber.plant(CrashlyticsTree(logLevel))
	}
}
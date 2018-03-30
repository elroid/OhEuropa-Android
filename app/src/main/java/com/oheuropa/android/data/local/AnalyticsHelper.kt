package com.oheuropa.android.data.local

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.github.ajalt.timberkt.*
import io.fabric.sdk.android.Fabric
import timber.log.Timber

/**
 * Created Date: 28/03/2018 12:01
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class AnalyticsHelper {
	companion object {
		fun logException(t: Throwable? = null, message: String) {
			Timber.w(t, if (t == null) message else "$message (${t.message})")

			//print localised stack trace to log...
			try {
				throw Exception(message, t)
			} catch (e: Exception) {
				w(e) { "Error:${e.message}"}
			}

			if (Fabric.isInitialized()) {
				try {
					Crashlytics.logException(t)
				} catch (e: IllegalStateException) {
					w { "Crashlytics not enabled, skipping exception log: ${e.message}"}
				}

			} else
				w { "Crashlytics not enabled, skipping exception log"}
		}

		fun logPlayUpdateRequired(){
			Answers.getInstance().logCustom(CustomEvent("Play update required"));
		}

		fun logBeaconUpdateComplete(){
			//todo remove once we are sure this is working
			i { "logBeaconUpdateComplete" }
			try {
				throw Exception("beacon update Complete")
			} catch (e: Exception) {
				Crashlytics.logException(e)
			}
		}
	}
}
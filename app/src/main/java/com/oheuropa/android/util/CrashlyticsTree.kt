package com.oheuropa.android.util

import android.util.Log
import com.github.ajalt.timberkt.w
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import java.util.Date


/**
 * Created Date: 28/03/2018 11:23
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class CrashlyticsTree(val logLevel: Int) : Timber.Tree() {

	companion object {
		fun withCrashlytics(action: FirebaseCrashlytics.() -> Unit) {
			try {
				FirebaseCrashlytics.getInstance().apply(action)
			} catch (e: Throwable) {
				w(e) { "Crashlytics function failed: ${e.message}" }
			}
		}
	}

	override fun isLoggable(tag: String?, priority: Int): Boolean {
		return priority >= logLevel
	}

	override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
		withCrashlytics {
			log("${levelAbbreviation(priority)}/${tag ?: ""}:$message")

			if (priority > Log.ERROR) {
				when (throwable) {
					null -> recordException(Exception(message))
					else -> {
						recordException(Exception(message, throwable))
					}
				}
			}
		}
	}

	private fun levelAbbreviation(priority: Int): String = when (priority) {
		Log.VERBOSE -> "V"
		Log.DEBUG -> "D"
		Log.INFO -> "I"
		Log.WARN -> "W"
		Log.ERROR -> "E"
		Log.ASSERT -> "WTF"
		else -> "X"
	}
}
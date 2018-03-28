package com.oheuropa.android.util

import android.util.Log
import com.crashlytics.android.Crashlytics
import timber.log.Timber
import java.util.*


/**
 * Created Date: 28/03/2018 11:23
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class CrashlyticsTree(val logLevel: Int) : Timber.Tree() {
	override fun isLoggable(tag: String?, priority: Int): Boolean {
		return priority >= logLevel
	}

	override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
		//15:47:28.123 - ClassName: DEBUG/My Message here
		var msg = Date().toString()
		msg += " - "
		if (tag != null) msg += "$tag: "
		msg += print(priority) + "/"
		msg += message

		if (t != null) {
			//add \nStack:...
			val elements = t.stackTrace
			msg += "\nStack:"
			for (element in elements) {
				msg += "\n\t" + element
			}
		}
		Crashlytics.log(tag + ": " + print(priority) + "/" + msg)
	}

	private fun print(priority: Int): String {
		return when (priority) {
			Log.VERBOSE -> "TRACE"
			Log.DEBUG -> "DEBUG"
			Log.WARN -> "WARN "
			Log.ERROR -> "ERROR"
			else -> "? ($priority)"
		}
	}
}
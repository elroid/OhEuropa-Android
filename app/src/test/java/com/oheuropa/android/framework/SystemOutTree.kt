package com.oheuropa.android.framework

import android.util.Log
import timber.log.Timber
import java.time.format.DateTimeFormatter
import java.util.*

/**
 *
 * Class: com.oheuropa.android.framework.SystemOutTree
 * Project: OhEuropa-Android
 * Created Date: 08/03/2018 09:12
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class SystemOutTree constructor(val logLevel: Int) : Timber.Tree() {

	override fun isLoggable(tag: String?, priority: Int): Boolean {
		return priority >= logLevel
	}

	override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
		//15:47:28.123 - ClassName: DEBUG/My Message here
		//String msg = new Date().toString();
		var msg = GregorianCalendar().toZonedDateTime()
			.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
		if (tag != null)
			msg += " - $tag"
		msg += ": "
		msg += print(priority) + "/"
		msg += message

		println(msg)
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
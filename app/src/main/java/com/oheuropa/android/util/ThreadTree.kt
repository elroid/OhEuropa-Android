package com.oheuropa.android.util

import android.util.Log
import com.crashlytics.android.Crashlytics
import com.github.ajalt.timberkt.v
import timber.log.Timber
import java.util.*


/**
 * Created Date: 28/03/2018 11:23
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class ThreadTree(val logLevel: Int) : Timber.DebugTree() {
	override fun isLoggable(priority: Int): Boolean {
		return priority >= logLevel
	}

	override fun log(priority: Int, tag: String?, msg: String, t: Throwable?) {
		var finalTag = tag
		if(finalTag == null) finalTag = ""
		val thread = if(GenUtils.isUIThread()) "MAIN-THREAD" else getThreadName()
		finalTag += " ($thread)"
		//Crashlytics.log(tag + ": " + print(priority) + "/" + msg)
		when (priority) {
			Log.VERBOSE -> Log.v(finalTag, msg)
			Log.DEBUG -> Log.d(finalTag, msg)
			Log.INFO -> Log.i(finalTag, msg)
			Log.WARN -> Log.w(finalTag, msg, t)
			Log.ERROR -> Log.e(finalTag, msg, t)
			else -> Log.e(finalTag+"? ($priority)", msg)
		}
	}

	private fun getThreadName():String{
		val threadString = Thread.currentThread().toString()
		return getFirstStringBetweenDelims(threadString, "{", "}") ?: threadString
	}

	private fun getFirstStringBetweenDelims(str: String, delim1: String, delim2: String = delim1, startIndex: Int = 0): String? {
		var string = str
		var result: String? = null
		string = string.substring(startIndex)
		val index1 = string.indexOf(delim1)
		val delim1Len = delim1.length
		if (index1 < 0)
			return null
		else {
			var index2 = string.indexOf(delim2, index1 + delim1Len)
			if (index2 > index1) result = string.substring(index1 + delim1Len, index2)
		}
		v { "getFirstStringBetweenDelims($str, $delim1, $delim2, $startIndex):$result" }
		return result
	}
}
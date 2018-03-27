package com.oheuropa.android.util

/**
 * Created Date: 22/03/2018 12:12
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class GenUtils {
	companion object {
		fun limit360(bearing: Float): Float {
			var result = bearing
			while (result < 0) result += 360
			while (result >= 360) result -= 360
			return result
		}

		fun printCallingMethod(): String {
			return printMethod(3, 0)
		}

		private fun printMethod(start: Int, levels: Int): String {
			try {
				throw Exception("Stack trace: (" + Thread.currentThread() + ")")
			} catch (e: Exception) {
				val elems = e.stackTrace
				return if (levels == 0)
					elems[start].toString()
				else {
					val r = StringBuilder("Methods:")
					var i = start
					while (i < elems.size && i < start + levels) {
						val elem = elems[i]
						r.append("\n[").append(i).append("]: ").append(elem)
						i++
					}
					r.toString()
				}
			}

		}
	}
}
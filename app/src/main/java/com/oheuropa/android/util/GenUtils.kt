package com.oheuropa.android.util

/**
 * Created by elroid on 22/03/2018.
 */
class GenUtils {
	companion object {
		fun limit360(bearing: Float): Float {
			var result = bearing
			while (result < 0) result += 360
			while (result >= 360) result -= 360
			return result
		}
	}
}
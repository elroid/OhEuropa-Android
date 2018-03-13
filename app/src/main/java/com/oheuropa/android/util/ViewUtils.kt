package com.oheuropa.android.util

import android.content.res.Resources
import timber.log.Timber
import timber.log.Timber.w

/**
 *
 * Class: com.oheuropa.android.util.ViewUtils
 * Project: OhEuropa-Android
 * Created Date: 05/03/2018 16:11
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class ViewUtils {
	companion object {
		fun pxToDp(px: Float): Float {
			val densityDpi = Resources.getSystem().displayMetrics.densityDpi.toFloat()
			return px / (densityDpi / 160f)
		}

		fun dpToPx(dp: Int): Int {
			val density = Resources.getSystem().displayMetrics.density
			return Math.round(dp * density)
		}

		fun getWidthAtHeight(w: Int, h: Int, newH: Int): Int {
			return newH * w / h
		}

		fun getHeightAtWidth(w: Int, h: Int, newW: Int): Int {
			return newW * h / w
		}

		fun getScreenWidth(): Int {
			return getScreenDim(true)
		}

		fun getScreenHeight(): Int {
			return getScreenDim(false)
		}

		private fun getScreenDim(width: Boolean): Int {
			return try {
				val w = Resources.getSystem().displayMetrics.widthPixels
				val h = Resources.getSystem().displayMetrics.heightPixels
				if (width) w else h
			} catch (e: Exception) {
				w(e, "Problem getting screen width - returning 0")
				0
			}
		}
	}
}
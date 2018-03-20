package com.oheuropa.android.util

import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.support.annotation.Dimension
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import com.github.ajalt.timberkt.w


/**
 *
 * Class: com.oheuropa.android.util.ViewUtils
 * Project: OhEuropa-Android
 * Created Date: 05/03/2018 16:11
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") //utilities class
class ViewUtils {
	companion object {
		fun pxToDp(px: Float): Float {
			val densityDpi = Resources.getSystem().displayMetrics.densityDpi.toFloat()
			return px / (densityDpi / 160f)
		}

		fun dpToPx(dp: Int): Int {
			return Math.round(dpToPxF(dp.toFloat()))
		}

		fun dpToPxF(dp: Float): Float {
			val density = Resources.getSystem().displayMetrics.density
			return dp * density
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
			} catch (ex: Exception) {
				w(ex) { "Problem getting screen width - returning 0" }
				0
			}
		}

		fun handler(): Handler {
			return Handler(Looper.getMainLooper())
		}

		fun setDimensions(view: View, @Dimension width: Int, @Dimension height: Int) {
			setDimensions(view, view.parent as View, width, height)
		}

		fun setDimensions(view: View, parent: View?, @Dimension width: Int, @Dimension height: Int) {
			var params = view.layoutParams
			if (params == null) {
				params = ViewGroup.LayoutParams(width, height)
				if (parent != null) {
					if (parent is AbsListView)
						params = AbsListView.LayoutParams(width, height)
					//todo other types
				}
				view.layoutParams = params
			} else {
				params.width = width
				params.height = height
			}
			view.requestLayout()
		}
	}
}
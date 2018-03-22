package com.oheuropa.android.ui.compass

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import com.oheuropa.android.R
import com.oheuropa.android.util.ViewUtils

/**
 * Class: com.oheuropa.android.ui.compass.CompassView
 * Project: OhEuropa-Android
 * Created Date: 19/03/2018 13:17
 *
 * @author [Elliot Long](mailto:e@elroid.com)
 * Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class CompassWaveView constructor(context: Context, attrs: AttributeSet? = null)
	: CompassView(context, attrs) {

	private val bitmapPaint = Paint()
	private var wavesBitmap: Bitmap? = null

	override fun getOuterCircleColour() = R.color.compass_inactive
	override fun getInnerCircleColour() = R.color.compass_active

	override fun onSizeChanged(measuredWidth: Int, measuredHeight: Int, oldWidth: Int, oldh: Int) {
		super.onSizeChanged(measuredWidth, measuredHeight, oldWidth, oldh)

		//retrieve, resize and tint waves image
		val bigWavesBmp = BitmapFactory.decodeResource(resources, R.drawable.compass_inner_waves)
		val tintColour = ContextCompat.getColor(context, R.color.compass_inactive)
		val w = innerRadius.toInt() * 2
		val h = ViewUtils.getHeightAtWidth(bigWavesBmp.width, bigWavesBmp.height, w)
		val resizedBitmap = getResizedBitmap(bigWavesBmp, w, h)
		wavesBitmap = tintImage(resizedBitmap, tintColour)
	}

	private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
		val width = bm.width
		val height = bm.height
		val scaleWidth = newWidth.toFloat() / width
		val scaleHeight = newHeight.toFloat() / height
		val matrix = Matrix()
		matrix.postScale(scaleWidth, scaleHeight)
		val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
		bm.recycle()
		return resizedBitmap
	}

	private fun tintImage(bitmap: Bitmap, color: Int): Bitmap {
		val paint = Paint()
		paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
		val bitmapResult = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
		val canvas = Canvas(bitmapResult)
		canvas.drawBitmap(bitmap, 0f, 0f, paint)
		return bitmapResult
	}

	override fun drawCompass(canvas: Canvas) {
		if (wavesBitmap != null)//draw waves
			canvas.drawBitmap(wavesBitmap, xCentre - innerRadius, innerRadius / 2,
				bitmapPaint)
	}
}
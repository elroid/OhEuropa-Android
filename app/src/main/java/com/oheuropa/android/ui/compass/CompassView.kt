package com.oheuropa.android.ui.compass

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.w
import com.oheuropa.android.R
import com.oheuropa.android.util.ViewUtils
import com.oheuropa.android.util.ViewUtils.Companion.dpToPxF


/**
 * Class: com.oheuropa.android.ui.compass.CompassView
 * Project: OhEuropa-Android
 * Created Date: 19/03/2018 13:17
 *
 * @author [Elliot Long](mailto:e@elroid.com)
 * Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class CompassView constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

	private var northAngle: Float = 0f
	private var beaconAngle: Float = 0f
	private val outerPaint = Paint()
	private val innerPaint = Paint()
	private val trianglePaint = Paint()
	private val northPaint = Paint()
	private val bitmapPaint = Paint()
	private val north = "N"
	private var northWidth = 0f
	private val percentOuter = 0.78f
	private val percentInner = 0.71f
	private var bigWavesBmp: Bitmap? = null
	private var wavesBitmap: Bitmap? = null
	private var tintColour: Int? = null

	init {
		tintColour = ContextCompat.getColor(getContext(), R.color.compass_inactive)
		outerPaint.color = ContextCompat.getColor(getContext(), R.color.compass_active)
		innerPaint.color = ContextCompat.getColor(getContext(), R.color.compass_inactive)
		trianglePaint.color = ContextCompat.getColor(getContext(), R.color.white)
		trianglePaint.strokeWidth = dpToPxF(2f)
		northPaint.color = ContextCompat.getColor(getContext(), R.color.white)

		northPaint.textSize = dpToPxF(25f)
		val charWidths = FloatArray(1)
		val read = northPaint.getTextWidths(north, charWidths)
		d { "read($read) lengths:$charWidths" }
		northWidth = charWidths[0]

		bigWavesBmp = BitmapFactory.decodeResource(resources, R.drawable.compass_inner_waves)
	}

	private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
		val width = bm.width
		val height = bm.height
		val scaleWidth = newWidth.toFloat() / width
		val scaleHeight = newHeight.toFloat() / height
		// CREATE A MATRIX FOR THE MANIPULATION
		val matrix = Matrix()
		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight)

		// "RECREATE" THE NEW BITMAP
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

	fun setAngles(northAngle: Float, beaconAngle: Float) {
		//d { "setAngles($northAngle, $beaconAngle)" }
		this.northAngle = northAngle
		this.beaconAngle = beaconAngle
		invalidate()
	}

	var compassEnabled: Boolean = true
		set(value) {
			val changed = field != value
			field = value
			if (changed) invalidate()
		}

	override fun onDraw(canvas: Canvas?) {

		try {
			if (canvas == null) return

			if (compassEnabled)
				drawCompass(canvas)
			else
				drawWaves(canvas)


		} catch (ex: Exception) {
			w(ex) { "error onDraw" }
		}
	}

	private fun drawWaves(canvas: Canvas) {
		val xCentre = measuredWidth / 2f
		val yCentre = measuredHeight / 2f

		val outerRadius = measuredWidth * percentOuter / 2
		val innerRadius = measuredWidth * percentInner / 2

		//draw ring
		canvas.drawCircle(xCentre, yCentre, outerRadius, innerPaint)

		//draw inner circle
		canvas.drawCircle(xCentre, yCentre, innerRadius, outerPaint)

		if (wavesBitmap == null) {
			if (bigWavesBmp != null) {
				val w = innerRadius.toInt() * 2
				val h = ViewUtils.getHeightAtWidth(bigWavesBmp!!.width, bigWavesBmp!!.height, w)
				val resizedBitmap = getResizedBitmap(bigWavesBmp!!, w, h)
				if (tintColour != null)
					wavesBitmap = tintImage(resizedBitmap, tintColour!!)
			}
		}
		//draw waves
		canvas.drawBitmap(wavesBitmap, xCentre - innerRadius, innerRadius / 2, bitmapPaint)

	}

	private fun drawCompass(canvas: Canvas) {

		val xCentre = measuredWidth / 2f
		val yCentre = measuredHeight / 2f

		val outerRadius = measuredWidth * percentOuter / 2
		val innerRadius = measuredWidth * percentInner / 2

		//outer (beacon) group
		canvas.save()
		canvas.rotate(beaconAngle, xCentre, yCentre)

		//draw arrow
		val arrowWidth = measuredWidth / 15
		val arrowHeight = measuredWidth / 15
		val arrowDist = measuredWidth / 2.4f
		val blx = xCentre - arrowWidth / 2
		val by = yCentre - arrowDist
		val ty = yCentre - arrowDist - arrowHeight
		val brx = xCentre + arrowWidth / 2
		canvas.drawLine(blx, by, xCentre, ty, trianglePaint)//BL-T
		canvas.drawLine(xCentre, ty, brx, by, trianglePaint)//T-BR
		canvas.drawLine(brx, by, blx, by, trianglePaint)//BR-BL

		//draw ring
		canvas.drawCircle(xCentre, yCentre, outerRadius, outerPaint)

		//draw ticks

		//done
		canvas.restore()

		//inner (compass) group
		canvas.save()
		canvas.rotate(northAngle, xCentre, yCentre)

		//draw inner circle
		canvas.drawCircle(xCentre, yCentre, innerRadius, innerPaint)

		//draw north
		val northDist = measuredWidth / 3.8f
		canvas.drawText(north, xCentre - northWidth / 2, yCentre - northDist, northPaint)

		//guide
		//canvas.drawLine(xCentre, yCentre, xCentre, northDist + arrowWidth, trianglePaint)

		//all done
		canvas.restore()
	}
}
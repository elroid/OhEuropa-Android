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
	private val tickPaint = Paint()
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
		tickPaint.color = ContextCompat.getColor(getContext(), R.color.white)
		tickPaint.strokeWidth = dpToPxF(3f)
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
		//todo add interpolator to smooth movement
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
			if (xCentre == 0f) return

			drawBaselineCircles(canvas)

			if (compassEnabled)
				drawCompass(canvas)
			else
				drawWaves(canvas)


		} catch (ex: Exception) {
			w(ex) { "error onDraw" }
		}
	}

	private var xCentre = 0f
	private var yCentre = 0f
	private var outerRadius = 0f
	private var innerRadius = 0f
	private var arrowBLX = 0f
	private var arrowBY = 0f
	private var arrowTY = 0f
	private var arrowBRX = 0f
	private var tickStartY = 0f
	private var tickEndY = 0f

	override fun onSizeChanged(measuredWidth: Int, measuredHeight: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(measuredWidth, measuredHeight, oldw, oldh)
		//centre
		xCentre = measuredWidth / 2f
		yCentre = measuredHeight / 2f

		//circles
		outerRadius = measuredWidth * percentOuter / 2
		innerRadius = measuredWidth * percentInner / 2

		//arrow
		val arrowWidth = measuredWidth / 15
		val arrowHeight = measuredWidth / 15
		val arrowDist = measuredWidth / 2.4f
		arrowBLX = xCentre - arrowWidth / 2
		arrowBY = yCentre - arrowDist
		arrowTY = yCentre - arrowDist - arrowHeight
		arrowBRX = xCentre + arrowWidth / 2

		//ticks
		val tickLength = measuredWidth / 40
		val tickDist = innerRadius
		tickStartY = yCentre - tickDist
		tickEndY = tickStartY - tickLength

		//waves image
		if (bigWavesBmp != null) {
			val w = innerRadius.toInt() * 2
			val h = ViewUtils.getHeightAtWidth(bigWavesBmp!!.width, bigWavesBmp!!.height, w)
			val resizedBitmap = getResizedBitmap(bigWavesBmp!!, w, h)
			if (tintColour != null)
				wavesBitmap = tintImage(resizedBitmap, tintColour!!)
		}
	}

	private fun drawBaselineCircles(canvas: Canvas) {
		val outer = when (compassEnabled) {
			true -> outerPaint
			false -> innerPaint
		}
		val inner = when (compassEnabled) {
			true -> innerPaint
			false -> outerPaint
		}
		//draw outer circle (ring)
		canvas.drawCircle(xCentre, yCentre, outerRadius, outer)

		//draw inner circle
		canvas.drawCircle(xCentre, yCentre, innerRadius, inner)
	}

	private fun drawWaves(canvas: Canvas) {
		if (wavesBitmap != null)//draw waves
			canvas.drawBitmap(wavesBitmap, xCentre - innerRadius, innerRadius / 2, bitmapPaint)
	}

	private fun drawCompass(canvas: Canvas) {
		//outer group (points to beacon)////////////////////////////////////////////////////////////
		canvas.save()
		canvas.rotate(beaconAngle, xCentre, yCentre)
		//draw arrow
		canvas.drawLine(arrowBLX, arrowBY, xCentre, arrowTY, trianglePaint)//BL-T
		canvas.drawLine(xCentre, arrowTY, arrowBRX, arrowBY, trianglePaint)//T-BR
		canvas.drawLine(arrowBRX, arrowBY, arrowBLX, arrowBY, trianglePaint)//BR-BL
		//draw ticks
		for (i in 0 until 360 step 10) {
			canvas.rotate(10f, xCentre, yCentre)
			canvas.drawLine(xCentre, tickStartY, xCentre, tickEndY, tickPaint)
		}
		canvas.restore()

		//inner group (points to north)/////////////////////////////////////////////////////////////
		canvas.save()
		canvas.rotate(northAngle, xCentre, yCentre)
		//draw north
		val northDist = measuredWidth / 3.8f
		canvas.drawText(north, xCentre - northWidth / 2, yCentre - northDist, northPaint)
		canvas.restore()
	}
}
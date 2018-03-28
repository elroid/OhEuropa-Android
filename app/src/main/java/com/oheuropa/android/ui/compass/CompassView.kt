package com.oheuropa.android.ui.compass

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.w
import com.oheuropa.android.R
import com.oheuropa.android.util.ViewUtils.Companion.dpToPxF


/**
 * Class: com.oheuropa.android.ui.compass.CompassView
 * Project: OhEuropa-Android
 * Created Date: 19/03/2018 13:17
 *
 * @author [Elliot Long](mailto:e@elroid.com)
 * Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
open class CompassView constructor(context: Context, attrs: AttributeSet? = null)
	: View(context, attrs) {

	private var northAngle: Float = 0f
	private var beaconAngle: Float = 0f
	private val outerPaint = Paint()
	private val innerPaint = Paint()
	private val trianglePaint = Paint()
	private val tickPaint = Paint()
	private val northPaint = Paint()

	private val north = "N"
	private var northWidth = 0f

	protected var xCentre = 0f
	private var yCentre = 0f
	private var outerRadius = 0f
	protected var innerRadius = 0f
	private var arrowBLX = 0f
	private var arrowBY = 0f
	private var arrowTY = 0f
	private var arrowBRX = 0f
	private var tickStartY = 0f
	private var tickEndY = 0f

	init {
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
	}

	protected open fun getOuterCircleColour() = R.color.compass_active
	protected open fun getInnerCircleColour() = R.color.compass_inactive

	fun setAngles(northAngle: Float, beaconAngle: Float) {
		//d { "setAngles($northAngle, $beaconAngle)" }
		this.northAngle = northAngle
		this.beaconAngle = beaconAngle
		invalidate()
		//todo add interpolator to smooth movement
	}

	override fun onSizeChanged(measuredWidth: Int, measuredHeight: Int,
							   oldWidth: Int, oldHeight: Int) {
		super.onSizeChanged(measuredWidth, measuredHeight, oldWidth, oldHeight)
		//centre
		xCentre = measuredWidth / 2f
		yCentre = measuredHeight / 2f

		//circles
		val percentOuter = 0.78f
		val percentInner = 0.71f
		outerRadius = measuredWidth * percentOuter / 2
		innerRadius = measuredWidth * percentInner / 2
		outerPaint.color = ContextCompat.getColor(context, getOuterCircleColour())
		innerPaint.color = ContextCompat.getColor(context, getInnerCircleColour())

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
	}

	override fun onDraw(canvas: Canvas?) {

		try {
			if (canvas == null) return
			if (xCentre == 0f) return

			drawBaselineCircles(canvas)
			drawCompass(canvas)

		} catch (ex: Exception) {
			w(ex) { "error onDraw" }
		}
	}

	private fun drawBaselineCircles(canvas: Canvas) {
		//draw outer circle (ring)
		canvas.drawCircle(xCentre, yCentre, outerRadius, outerPaint)

		//draw inner circle
		canvas.drawCircle(xCentre, yCentre, innerRadius, innerPaint)
	}

	protected open fun drawCompass(canvas: Canvas) {
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
package com.oheuropa.android.data.local

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.github.ajalt.timberkt.d
import com.oheuropa.android.domain.CompassComponent
import com.oheuropa.android.domain.Constants
import com.oheuropa.android.domain.USE_MOCK_COMPASS_READINGS
import com.oheuropa.android.util.GenUtils.Companion.limit360
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import org.reactivestreams.Publisher
import java.util.concurrent.Callable

/**
 *
 * Class: com.oheuropa.android.data.local.CompassProvider
 * Project: OhEuropa-Android
 * Created Date: 15/03/2018 15:13
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class CompassProvider constructor(ctx: Context) : CompassComponent {

	private val sensorManager: SensorManager = ctx.getSystemService(SENSOR_SERVICE) as SensorManager
	//this is lovely but breaks the build on kitkat and below...
	//private val sensorManager: SensorManager = ctx.systemService()

	override fun listenToCompass(): Flowable<Float> {
		@Suppress("ConstantConditionIf")
		return if (Constants.isDebug(USE_MOCK_COMPASS_READINGS))
			mockCompassFlowable()
		else if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null)
			RotationObservable(sensorManager).flow()
		else
			OrientationObservable(sensorManager).flow()
	}

	private fun mockCompassFlowable(): Flowable<Float> {
		return Flowable.create({ e: FlowableEmitter<Float> ->
			d { "creating flowable" }
			var curr = 0f
			val STEP = 20f
			while (!e.isCancelled) {
				Thread.sleep(250)
				val increment = limit360((Math.random().toFloat() * STEP - STEP / 2))
				curr += increment
				e.onNext(curr)
			}
		}, BackpressureStrategy.LATEST)
	}

	/**
	 * Uses Sensor.TYPE_ROTATION_VECTOR which should be used if available
	 */
	private class RotationObservable constructor(mgr: SensorManager) : SensorObservable(mgr) {
		override val sensorType = Sensor.TYPE_ROTATION_VECTOR

		override fun convertToDegrees(eventValues: FloatArray): Float {
			val rotationV = FloatArray(16)
			SensorManager.getRotationMatrixFromVector(rotationV, eventValues)
			val orientationValuesV = FloatArray(3)
			val orient = SensorManager.getOrientation(rotationV, orientationValuesV)
			return ((Math.toDegrees(orient[0].toDouble()) + 360) % 360).toFloat()
		}
	}

	/**
	 * Uses Sensor.TYPE_ORIENTATION - which is deprecated but more widely supported
	 */
	private class OrientationObservable constructor(mgr: SensorManager) : SensorObservable(mgr) {
		@Suppress("DEPRECATION") //yes we know...
		override val sensorType = Sensor.TYPE_ORIENTATION

		override fun convertToDegrees(eventValues: FloatArray): Float {
			return eventValues[0]
		}
	}

	private abstract class SensorObservable(mgr: SensorManager) {

		internal abstract val sensorType: Int

		internal abstract fun convertToDegrees(eventValues: FloatArray): Float

		var callable: Callable<Sensor> = Callable { mgr.getDefaultSensor(sensorType) }

		private var listener: Listener? = null

		internal inner class Listener(var emitter: FlowableEmitter<Float>) : SensorEventListener {

			override fun onSensorChanged(event: SensorEvent) {
				emitter.onNext(convertToDegrees(event.values))
			}

			override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
				//do nothing
			}
		}

		//function to create the Flowable
		var function: Function<Sensor, Publisher<Float>> = Function { sensor ->
			Flowable.create({ e ->
				listener = Listener(e)
				mgr.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
			}, BackpressureStrategy.LATEST)

		}
		//disposes the listener on dispose
		var disposer: Consumer<Sensor> = Consumer {
			mgr.unregisterListener(listener)
		}

		fun flow(): Flowable<Float> {
			return Flowable.using(callable, function, disposer)
		}
	}
}
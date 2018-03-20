package com.oheuropa.android.data.local

import android.content.Context
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.w
import com.github.ajalt.timberkt.wtf
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.oheuropa.android.domain.LocationComponent
import com.oheuropa.android.domain.USE_MOCK_USER_LOCATION
import com.oheuropa.android.model.Coordinate
import com.oheuropa.android.util.ViewUtils
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

/**
 *
 * Class: com.oheuropa.android.data.local.LocationProvider
 * Project: OhEuropa-Android
 * Created Date: 14/03/2018 15:01
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class LocationProvider constructor(private val ctx: Context) : LocationComponent {

	private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
	private var locationObserver: Observable<Coordinate>? = null
	private lateinit var locationCallback: LocationCallback

	override fun start(listener: LocationComponent.LocationStartListener) {
		try {
			val locationRequest = LocationRequest().apply {
				interval = 1000
				fastestInterval = 100
				priority = LocationRequest.PRIORITY_HIGH_ACCURACY
			}
			val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
			val client: SettingsClient = LocationServices.getSettingsClient(ctx)
			val task = client.checkLocationSettings(builder.build())
			task.addOnSuccessListener { settingsResponse ->
				d { "task.onConnected: $settingsResponse" }
				// All location settings are satisfied. Go nuts.
				locationObserver = when (USE_MOCK_USER_LOCATION) {
					false -> createLocationObserver(locationRequest)
					true -> {
						//todo debug test methods
						createWanderTest()
						//createStandListenTest()
					}
				}
				listener.onConnected()
			}

			task.addOnFailureListener { exception ->
				w { "task.failure: $exception" }
				if (exception is ResolvableApiException) {
					listener.onApiError(exception)
				} else
					listener.onError(exception)
			}
		} catch (ex: Exception) {
			w(ex) { "Exception" }
			listener.onError(ex)
		}
	}

	override fun stop() {
		d { "remove location updates" }
		fusedLocationClient.removeLocationUpdates(locationCallback)
	}

	override fun locationListener(): Observable<Coordinate> {
		return locationObserver ?: throw IllegalStateException("You must call start first")
	}

	private fun createLocationObserver(locationRequest: LocationRequest): Observable<Coordinate> {
		return Observable.create({ e: ObservableEmitter<Coordinate> ->

			try {
				fusedLocationClient.lastLocation.addOnSuccessListener {
					d { "got last location: $it" }
					e.onNext(Coordinate(it))
				}

				locationCallback = object : LocationCallback() {
					override fun onLocationResult(locationResult: LocationResult?) {
						locationResult ?: return
						d { "got location result: $locationResult" }
						for (location in locationResult.locations) {
							e.onNext(Coordinate(location))
						}
					}
				}
				d { "requesting location updates..." }
				ViewUtils.handler().post {
					fusedLocationClient.requestLocationUpdates(locationRequest,
						locationCallback, null)
				}

			} catch (e: SecurityException) {
				wtf(e) { "ignoring security exception (why hasn't this already been handled?)" }
			}
		})
	}

	@Suppress("unused")
	private fun createWanderTest(): Observable<Coordinate> {
		return Observable.create({ e ->
			Thread({
				val streetEnd = Coordinate(51.469002, -2.550491)
				val chocEnd = Coordinate(51.468641, -2.551070)
				val steps = 16
				val delayMs = 2000L
				val coordinateList = split(chocEnd, streetEnd, steps)
				coordinateList.addAll(split(streetEnd, chocEnd, steps))

				while (!e.isDisposed) {
					coordinateList.forEach {
						if (!e.isDisposed) {
							e.onNext(it)
							Thread.sleep(delayMs)
						}
					}
				}
			}).start()
		})
	}

	@Suppress("unused")
	private fun createStandListenTest(): Observable<Coordinate> {
		return Observable.create({ e ->
			Thread({
				val streetEnd = Coordinate(51.469002, -2.550491)
				e.onNext(streetEnd)
			}).start()
		})
	}

	private fun split(start: Coordinate, end: Coordinate, steps: Int): MutableList<Coordinate> {
		val lats = split(start.latitude, end.latitude, steps)
		val longs = split(start.longitude, end.longitude, steps)
		val result = mutableListOf<Coordinate>()
		for (i in 0 until steps) {
			result.add(Coordinate(lats[i], longs[i]))
		}
		return result
	}

	private fun split(start: Double, end: Double, steps: Int): DoubleArray {
		val diff = end - start
		val increment = diff / steps
		val result = DoubleArray(steps)
		var current = start
		for (i in 0 until steps) {
			result[i] = current
			current += increment
		}
		return result
	}
}
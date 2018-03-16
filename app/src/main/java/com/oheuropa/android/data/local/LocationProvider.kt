package com.oheuropa.android.data.local

import android.content.Context
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.w
import com.github.ajalt.timberkt.wtf
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.oheuropa.android.domain.LocationComponent
import com.oheuropa.android.model.Coordinate
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
		d { "start()" }
		try {
			val locationRequest = LocationRequest().apply {
				interval = 5000
				fastestInterval = 1000
				priority = LocationRequest.PRIORITY_HIGH_ACCURACY
			}
			val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
			val client: SettingsClient = LocationServices.getSettingsClient(ctx)
			val task = client.checkLocationSettings(builder.build())
			task.addOnSuccessListener { settingsResponse ->
				d { "task.onConnected: $settingsResponse" }
				// All location settings are satisfied. Go nuts.
				locationObserver = Observable.create({ e: ObservableEmitter<Coordinate> ->

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
						fusedLocationClient.requestLocationUpdates(locationRequest,
							locationCallback, null)

					} catch (e: SecurityException) {
						wtf(e) { "ignoring security exception (why hasn't this already been handled?)" }
					}
				})
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
	/*override fun locationListener(): Observable<Coordinate> {
		//todo placeholder output
		return Observable.create({
			it.onNext(Coordinate(51.46858, -2.551146, 30))
			Thread.sleep(2000)
			it.onNext(Coordinate(51.46858, -2.551146, 20))
			*//*Thread.sleep(2000)
			it.onNext(Coordinate(51.46858, -2.551146, 10))
			Thread.sleep(2000)
			it.onNext(Coordinate(51.45858, -2.561146, 50))
			Thread.sleep(2000)
			it.onNext(Coordinate(51.45858, -2.561146, 20))*//*

		})
	}*/
}
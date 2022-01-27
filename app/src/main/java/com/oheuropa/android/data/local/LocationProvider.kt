package com.oheuropa.android.data.local

import android.content.Context
import android.os.HandlerThread
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.v
import com.github.ajalt.timberkt.w
import com.github.ajalt.timberkt.wtf
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.oheuropa.android.domain.Constants
import com.oheuropa.android.domain.LocationComponent
import com.oheuropa.android.domain.USE_MOCK_USER_LOCATION
import com.oheuropa.android.model.Coordinate
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable
import kotlin.concurrent.thread

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
    private val locationObserver: Observable<Coordinate> by lazy { createLocationObserver() }
    private val locationRequest = LocationRequest.create().apply {
        interval = 1000
        fastestInterval = 100
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun start(listener: LocationComponent.LocationStartListener) {
        try {

            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            val client: SettingsClient = LocationServices.getSettingsClient(ctx)
            val task = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener { settingsResponse ->
                d { "task.onConnected: $settingsResponse" }
                // All location settings are satisfied. Go nuts.
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

    override fun locationListener(): Observable<Coordinate> {
        return locationObserver
    }

    private fun createLocationObserver(): Observable<Coordinate> {
        return when (Constants.isDebug(USE_MOCK_USER_LOCATION)) {
            true -> {
                createWanderTest()//test user wandering back and forth
                //createStandListenTest()//tes user listening in centre
            }
            false -> {
                LocationObservable(fusedLocationClient).observe(locationRequest)
            }
        }
    }

    private class LocationObservable constructor(
        private val fusedLocationClient: FusedLocationProviderClient
    ) : Disposable {
        val locationCallback: LocationCallback
        val handlerThread: HandlerThread
        var disposed = false
        var e: ObservableEmitter<Coordinate>? = null

        init {
            d { "creating LocationObservable..." }
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    d { "got location result: $locationResult" }
                    for (location in locationResult.locations) {
                        onNext(Coordinate(location))
                    }
                }
            }
            handlerThread = HandlerThread("LocationHandler")
        }

        fun observe(locationRequest: LocationRequest): Observable<Coordinate> {
            return Observable.create { emitter: ObservableEmitter<Coordinate> ->
                disposed = false
                e = emitter
                emitter.setDisposable(this)
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        d { "got last location: $it" }
                        if (it != null)
                            onNext(Coordinate(it))
                    }
                    d { "requesting location updates..." }
                    handlerThread.start()
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, handlerThread.looper)
                } catch (e: SecurityException) {
                    wtf(e) { "ignoring security exception (why hasn't this already been handled?)" }
                }
            }
        }

        fun onNext(coord: Coordinate) {
            e?.onNext(coord)
        }

        override fun isDisposed(): Boolean {
            return disposed
        }

        override fun dispose() {
            v { "disposing locationObserver, stopping location provider(disposed:$disposed)..." }
            if (!disposed) {
                fusedLocationClient.removeLocationUpdates(locationCallback)
                handlerThread.quitSafely()
            }
            disposed = true
        }
    }

    @Suppress("unused")
    private fun createWanderTest(): Observable<Coordinate> {
        return Observable.create { e ->
            thread {
                val westEnd = Coordinate(51.469002, -2.550491)
                val eastEnd = Coordinate(51.468641, -2.551070)//choc factory gate
//				val eastEnd = Coordinate(51.468296, -2.554005)//cemetery gate
                val steps = 16
                val delayMs = 2000L
                val coordinateList = split(eastEnd, westEnd, steps)
                coordinateList.addAll(split(westEnd, eastEnd, steps))

                while (!e.isDisposed) {
                    coordinateList.forEach {
                        if (!e.isDisposed) {
                            e.onNext(it)
                            Thread.sleep(delayMs)
                        }
                    }
                }
            }
        }
    }

    @Suppress("unused")
    private fun createStandListenTest(): Observable<Coordinate> {
        return Observable.create { e ->
            thread {
                val streetEnd = Coordinate(51.469002, -2.550491)
                e.onNext(streetEnd)
            }
        }
    }

    private fun split(start: Coordinate, end: Coordinate, steps: Int): MutableList<Coordinate> {
        val lats = split(start.latitude, end.latitude, steps)
        val longs = split(start.longitude, end.longitude, steps)
        val result = mutableListOf<Coordinate>()
        (0 until steps).mapTo(result) { Coordinate(lats[it], longs[it], Math.random() * 50) }
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
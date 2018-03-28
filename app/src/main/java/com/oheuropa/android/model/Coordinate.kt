package com.oheuropa.android.model

import android.location.Location
import com.google.android.gms.maps.model.LatLng

/**
 *
 * Class: com.oheuropa.android.model.Coordinate
 * Project: OhEuropa-Android
 * Created Date: 14/03/2018 13:44
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
data class Coordinate(
	val latitude: Double,
	val longitude: Double,
	val accuracy: Number = 0
) {
	constructor(lat: Float, lon: Float) : this(lat.toDouble(), lon.toDouble())
	constructor(loc: Location) : this(loc.latitude, loc.longitude, loc.accuracy)

	private val loc: Location by lazy {
		val location = Location("generated")
		location.latitude = latitude
		location.longitude = longitude
		location
	}

	fun isValid(): Boolean {
		return latitude != 0.toDouble() && longitude != 0.toDouble()
	}

	fun toLocation(): Location {
		return loc
	}

	fun toLatLng(): LatLng {
		return LatLng(latitude, longitude)
	}

	fun getDistanceMeters(loc: Coordinate): Float {
		return toLocation().distanceTo(loc.toLocation())
	}

	fun getDistanceAndBearing(other: Coordinate): Pair<Float, Float> {
		val results = FloatArray(3)
		Location.distanceBetween(latitude, longitude, other.latitude, other.longitude, results)
		return Pair(results[0], results[1])
	}
}
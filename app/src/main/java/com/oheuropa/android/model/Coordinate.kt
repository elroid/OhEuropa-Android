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
	val longitude: Double
) {
	constructor(lat: Float, lon: Float) : this(lat.toDouble(), lon.toDouble())

	private val loc: Location by lazy {
		val location = Location("generated")
		location.latitude = latitude
		location.longitude = longitude
		location
	}

	fun toLocation(): Location {
		return loc
	}

	fun toLatLng(): LatLng {
		return LatLng(latitude, longitude)
	}

	fun getDistanceMeters(loc: Coordinate): Float {
		return getDistanceMeters(loc.toLocation())
	}

	fun getDistanceMeters(loc: Location): Float {
		return toLocation().distanceTo(loc)
	}
}
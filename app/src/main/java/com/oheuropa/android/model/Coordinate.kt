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
	constructor(loc: LatLng) : this(loc.latitude, loc.longitude, 0f)

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

	fun toMinutesString(): String {
		val builder = StringBuilder()

		val latitudeDegrees = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS)
		val latitudeSplit =
			latitudeDegrees.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		builder.append(latitudeSplit[0])
		builder.append("° ")
		builder.append(latitudeSplit[1])
		builder.append("' ")

		var seconds = java.lang.Float.parseFloat(latitudeSplit[2])
		builder.append(Math.floor(seconds.toDouble()).toInt())
		builder.append("\" ")

		if (latitude < 0) {
			builder.append("S ")
		} else {
			builder.append("N ")
		}

		val longitudeDegrees = Location.convert(Math.abs(longitude), Location.FORMAT_SECONDS)
		val longitudeSplit =
			longitudeDegrees.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		builder.append(longitudeSplit[0])
		builder.append("° ")
		builder.append(longitudeSplit[1])
		builder.append("' ")

		seconds = java.lang.Float.parseFloat(longitudeSplit[2])
		builder.append(Math.floor(seconds.toDouble()).toInt())
		builder.append("\" ")

		if (longitude < 0) {
			builder.append("W")
		} else {
			builder.append("E")
		}

		return builder.toString()
	}
}
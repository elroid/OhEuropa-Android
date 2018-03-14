package com.oheuropa.android.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Beacon(
	@Id(assignable = true) var id: Long,
	val name: String,//"Alvington",
	val placeid: String = "",//"ALV1X",
	val lat: Float,
	val lng: Float,
	val datecreated: String = "",//"2018-03-01 23:20:00",
	val centerradius: Int = 40,
	val innerradius: Int = 60,
	val outerradius: Int = 100,
	val radioplays: Int = 0,
	val nearbys: Int = 0
) {
	fun getCoordinate(): Coordinate {
		return Coordinate(lat, lng)
	}
}
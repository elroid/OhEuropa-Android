package com.oheuropa.android.model

/**
 *
 * Class: com.oheuropa.android.model.BeaconLocation
 * Project: OhEuropa-Android
 * Created Date: 07/03/2018 17:36
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
data class BeaconLocation(val beacons: List<Beacon>, val myLocation: Coordinate) {
	fun getDistanceAndBearing(): Pair<Float, Float> {
		if (beacons.isEmpty()) return Pair(0f, 0f)
		return myLocation.getDistanceAndBearing(beacons[0].getCoordinate())
	}

	enum class CircleState {
		CENTRE, INNER, OUTER, NONE
	}

	fun getCircleState(): CircleState {
		if (!beacons.isEmpty()) {
			val beacon = beacons[0]
			val dist = getDistanceAndBearing().first
			when {
				dist < beacon.centerradius -> return CircleState.CENTRE
				dist < beacon.innerradius -> return CircleState.INNER
				dist < beacon.outerradius -> return CircleState.OUTER
			}
		}
		return CircleState.NONE
	}

	fun getPlaceId(): String {
		return beacons.getOrNull(0)?.placeid ?: ""
	}

	override fun toString(): String {
		return "BeaconLocation: placeid(${getPlaceId()}) dist(${getDistanceAndBearing().first}) " +
			"state(${getCircleState()})"
	}
}
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
data class BeaconLocation(
	val beacons: List<Beacon>, val myLocation: Coordinate
)
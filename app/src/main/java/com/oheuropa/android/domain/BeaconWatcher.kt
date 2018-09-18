package com.oheuropa.android.domain

import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.v
import com.oheuropa.android.data.DataManager
import com.oheuropa.android.model.Beacon
import com.oheuropa.android.model.BeaconLocation
import com.oheuropa.android.model.Coordinate
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.*


/**
 *
 * Class: com.oheuropa.android.domain.BeaconWatcher
 * Project: OhEuropa-Android
 * Created Date: 07/03/2018 11:59
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class BeaconWatcher constructor(
	private val dataManager: DataManager,
	private val locator: LocationComponent
) {

	fun followBeaconLocation(): Observable<BeaconLocation> {
		return beaconLocationObservable
	}

	private val beaconLocationObservable: Observable<BeaconLocation> by lazy {
		createBeaconLocationObservable().share()
	}

	private fun createBeaconLocationObservable(): Observable<BeaconLocation> {
		d { "CREATING beaconObservable" }
		val allBeacons = dataManager.followBeaconList()
		val currentLocation = locator.locationListener()
		return Observable.combineLatest<List<Beacon>, Coordinate, BeaconLocation>(allBeacons,
			currentLocation,
			BiFunction<List<Beacon>, Coordinate, BeaconLocation> { beacons, location ->
				//d { "got beacons($beacons) and location($location)" }
				Collections.sort<Beacon>(beacons, BeaconDistanceComparator(location))
				val result = BeaconLocation(beacons, location)
				v { "broadcasting:$result" }
				result
			})
	}

	private inner class BeaconDistanceComparator internal constructor(
		private val currentLocation: Coordinate) : Comparator<Beacon> {

		override fun compare(beacon1: Beacon, beacon2: Beacon): Int {
			return beacon1.getCoordinate().getDistanceMeters(currentLocation)
				.compareTo(beacon2.getCoordinate().getDistanceMeters(currentLocation))
		}
	}
}
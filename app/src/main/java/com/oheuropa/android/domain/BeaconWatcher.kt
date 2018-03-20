package com.oheuropa.android.domain

import com.fernandocejas.frodo.annotation.RxLogObservable
import com.github.ajalt.timberkt.d
import com.oheuropa.android.data.DataManager
import com.oheuropa.android.model.Beacon
import com.oheuropa.android.model.BeaconLocation
import com.oheuropa.android.model.Coordinate
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.*
import javax.inject.Inject


/**
 *
 * Class: com.oheuropa.android.domain.BeaconWatcher
 * Project: OhEuropa-Android
 * Created Date: 07/03/2018 11:59
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class BeaconWatcher @Inject constructor(
	private val dataManager: DataManager
) {
	@RxLogObservable
	fun followBeaconLocation(locator: LocationComponent): Observable<BeaconLocation> {
		d { "followBeaconLocation" }
		val beaconObservable = when(USE_MOCK_BEACON_LOCATIONS) {
			true -> dataManager.getTestBeaconList()
			false -> dataManager.followBeaconList()
		}
		return getBeaconLocationObservable(beaconObservable, locator.locationListener())
	}

	private fun getBeaconLocationObservable(
		allBeacons: Observable<List<Beacon>>,
		currentLocation: Observable<Coordinate>): Observable<BeaconLocation> {
		return Observable.combineLatest<List<Beacon>, Coordinate, BeaconLocation>(allBeacons,
			currentLocation,
			BiFunction<List<Beacon>, Coordinate, BeaconLocation> { beacons, location ->
				//d { "got beacons($beacons) and location($location)" }
				Collections.sort<Beacon>(beacons, BeaconDistanceComparator(location))
				BeaconLocation(beacons, location)
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
package com.oheuropa.android.domain

import android.location.Location
import com.fernandocejas.frodo.annotation.RxLogObservable
import com.oheuropa.android.data.DataManager
import com.oheuropa.android.model.BeaconLocation
import com.oheuropa.android.model.Model
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction
import java.util.*
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


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
	private val locator: LocationComponent,
	private val dataManager: DataManager
) {

	private val beaconObservable: Observable<BeaconLocation> by lazy {
		getBeaconLocationObservable(
			dataManager.getBeaconList(), locator.locationListener()
		)
	}

	@RxLogObservable
	fun followBeaconLocation(): Observable<BeaconLocation> {
		return beaconObservable
	}

	private fun getBeaconLocationObservable(
		allBeacons: Observable<List<Model.Beacon>>,
		currentLocation: Observable<Location>): Observable<BeaconLocation> {
		return Observable.combineLatest<List<Model.Beacon>, Location, BeaconLocation>(allBeacons,
			currentLocation,
			BiFunction<List<Model.Beacon>, Location, BeaconLocation> { beacons, location ->
				Collections.sort<Model.Beacon>(beacons, BeaconDistanceComparator(location))
				BeaconLocation(beacons[0], location)
			})
	}

	private inner class BeaconDistanceComparator internal constructor(
		private val currentLocation: Location) : Comparator<Model.Beacon> {
		private val loc1 = Location("")
		private val loc2 = Location("")

		override fun compare(beacon1: Model.Beacon, beacon2: Model.Beacon): Int {
			loc1.latitude = beacon1.lat.toDouble()
			loc1.longitude = beacon1.lng.toDouble()
			loc2.latitude = beacon2.lat.toDouble()
			loc2.longitude = beacon2.lng.toDouble()
			return java.lang.Float.valueOf(loc1.distanceTo(currentLocation))!!
				.compareTo(loc2.distanceTo(currentLocation))
		}
	}
}
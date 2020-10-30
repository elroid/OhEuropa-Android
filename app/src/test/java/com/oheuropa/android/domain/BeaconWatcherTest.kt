package com.oheuropa.android.domain

import com.github.ajalt.timberkt.d
import com.oheuropa.android.data.DataManager
import com.oheuropa.android.framework.RoboelectricTest
import com.oheuropa.android.model.Coordinate
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import io.reactivex.subjects.ReplaySubject
import org.junit.Test
import kotlin.test.assertEquals

/**
 *
 * Class: com.oheuropa.android.domain.BeaconWatcherTest
 * Project: OhEuropa-Android
 * Created Date: 08/03/2018 08:55
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class BeaconWatcherTest : RoboelectricTest() {

	@Test
	fun followBeaconLocation_givenNewClosestBeacon_returnNewBeacon() {

		val northLat = 51.6
		val midNorthLat = 51.5
		val midLat = 51.4
		val eastLon = -2.5
		val midEastLon = -2.6
		val midLon = -2.7
		val northPoint = Coordinate(northLat, midLon)
		val eastPoint = Coordinate(midLat, eastLon)
		val startLoc = Coordinate(midNorthLat, midLon)
		val endLoc = Coordinate(midLat, midEastLon)

		val locObs = ReplaySubject.create<Coordinate>()
		val locator = mockk<LocationComponent>{
			every { locationListener() } returns locObs
		}
		val beacon1 = createBeacon("One", 1, northPoint)
		val beacon2 = createBeacon("Two", 2, eastPoint)

		val beaconList = listOf(beacon1, beacon2)
		val listObs = Observable.just(beaconList)
			.doOnNext {
				d { "Outputting beacons:$it" }
			}
		val dataManager = mockk<DataManager> {
			every { followBeaconList() } returns listObs
		}

		val beaconWatcher = BeaconWatcher(dataManager, locator)

		//emit first location
		locObs.onNext(startLoc)

		//check that beacon1 is the closest
		val testObserver = beaconWatcher.followBeaconLocation().test()
		val closestBeacon1 = testObserver.values()[0]
		assertEquals(beacon1, closestBeacon1.beacons[0])

		//emit end loc
		locObs.onNext(endLoc)
		//check that beacon2 is now closest
		val closestBeacon2 = testObserver.values()[1]
		assertEquals(beacon2, closestBeacon2.beacons[0])
	}
}
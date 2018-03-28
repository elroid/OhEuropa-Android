package com.oheuropa.android.ui.map

import com.oheuropa.android.model.Beacon
import com.oheuropa.android.model.Coordinate
import com.oheuropa.android.ui.base.LocationEnabledPres
import com.oheuropa.android.ui.base.LocationEnabledView

/**
 *
 * Class: com.oheuropa.android.ui.map.MapContract
 * Project: OhEuropa-Android
 * Created Date: 14/03/2018 12:21
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
interface MapContract {
	interface View : LocationEnabledView {
		fun showBeacons(beacons: List<Beacon>)
		fun showMyLocation(loc: Coordinate)
		fun zoomTo(beacons: List<Beacon>, myLocation: Coordinate)
	}

	interface Presenter : LocationEnabledPres
}
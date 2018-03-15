package com.oheuropa.android.ui.map

import com.google.android.gms.common.api.ResolvableApiException
import com.oheuropa.android.model.Beacon
import com.oheuropa.android.model.Coordinate
import com.oheuropa.android.ui.base.BaseView

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
	interface View : BaseView {
		fun showBeacons(beacons: List<Beacon>)
		fun showMyLocation(loc: Coordinate)
		fun zoomTo(beacons: List<Beacon>, myLocation: Coordinate)
		fun askForPermissions()
		fun resolveApiIssue(ex: ResolvableApiException)
	}

	interface Presenter {
		fun start()
		fun stop()
	}
}
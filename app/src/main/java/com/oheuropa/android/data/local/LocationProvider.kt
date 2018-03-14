package com.oheuropa.android.data.local

import com.oheuropa.android.domain.LocationComponent
import com.oheuropa.android.model.Coordinate
import io.reactivex.Observable

/**
 *
 * Class: com.oheuropa.android.data.local.LocationProvider
 * Project: OhEuropa-Android
 * Created Date: 14/03/2018 15:01
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class LocationProvider : LocationComponent {

	override fun locationListener(): Observable<Coordinate> {
		//todo placeholder output
		return Observable.create({ it.onNext(Coordinate(51.46858, -2.551146)) })
	}
}
package com.oheuropa.android.domain

import com.oheuropa.android.model.Coordinate
import io.reactivex.Observable

/**
 *
 * Class: com.oheuropa.android.domain.LocationProvider
 * Project: OhEuropa-Android
 * Created Date: 07/03/2018 15:57
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
interface LocationComponent {
	fun locationListener(): Observable<Coordinate>
}
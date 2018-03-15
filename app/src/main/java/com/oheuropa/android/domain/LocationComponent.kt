package com.oheuropa.android.domain

import com.google.android.gms.common.api.ResolvableApiException
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

	interface LocationStartListener{
		fun onSuccess()
		fun onPermissionsError(ex:SecurityException)
		fun onApiError(ex: ResolvableApiException)
		fun onError(ex: Exception)
	}

	fun start(listener:LocationStartListener)
	fun stop()

	fun locationListener(): Observable<Coordinate>
}
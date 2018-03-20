package com.oheuropa.android.ui.base

import com.google.android.gms.common.api.ResolvableApiException
import com.oheuropa.android.domain.LocationComponent

/**
 *
 * Class: com.oheuropa.android.ui.base.LocationEnabledView
 * Project: OhEuropa-Android
 * Created Date: 16/03/2018 11:15
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
interface LocationEnabledView : BaseView {
	fun isLocationPermissionGranted(): Boolean
	fun requestLocationPermission()
	fun showLocationPermissionExplanation()
	fun resolveApiIssue(ex: ResolvableApiException)
	fun startAudioService(locator:LocationComponent)
}
package com.oheuropa.android.ui.base

import com.github.ajalt.timberkt.w
import com.google.android.gms.common.api.ResolvableApiException
import com.oheuropa.android.domain.LocationComponent

/**
 *
 * Class: com.oheuropa.android.ui.base.BasePres
 * Project: OhEuropa-Android
 * Created Date: 15/03/2018 19:25
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
abstract class LocationEnabledPresenter<out V : LocationEnabledView>(
	view: V,
	protected val locator: LocationComponent
) : BasePresenter<V>(view), LocationEnabledPres, LocationComponent.LocationStartListener {

	override fun start() {
		if (view.isLocationPermissionGranted()) {
			locator.start(this)
		} else {
			view.showLocationPermissionExplanation()
		}
	}

	override fun onConnected() {
		view.startAudioService(locator)
	}

	override fun onApiError(ex: ResolvableApiException) {
		w(ex) { "Api error" }
		view.resolveApiIssue(ex)
	}

	override fun onError(ex: Exception) {
		w(ex) { "General error" }
		view.showError(msg = ex.message)
	}
}
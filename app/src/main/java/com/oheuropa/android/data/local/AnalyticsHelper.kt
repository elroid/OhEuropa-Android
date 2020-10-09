package com.oheuropa.android.data.local

import android.content.Context
import com.github.ajalt.timberkt.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.oheuropa.android.model.BeaconLocation
import com.oheuropa.android.model.UserRequest
import timber.log.Timber
import javax.inject.Inject

/**
 * Created Date: 28/03/2018 12:01
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class AnalyticsHelper @Inject constructor(val ctx: Context) {

	private val firebaseAnalytics by lazy { Firebase.analytics }

	fun logPlayUpdateRequired() {
		firebaseAnalytics.logEvent("play_update_required", null)
	}

	fun logBeaconUpdateComplete() {
		i { "logBeaconUpdateComplete" }
	}

	fun logBeaconEntered(placeId: String, circleState: BeaconLocation.CircleState, action: UserRequest.Action) {
		try {
			if(action == UserRequest.Action.Entered) {
				firebaseAnalytics.logEvent("beacon_entered") {
					param("zone", circleState.toString())
					param("placeId", placeId)
				}
			}
		} catch(ex: Exception) {
			w(ex) { "Error logging beacon entry..." }
		}
	}
}
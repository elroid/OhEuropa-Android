package com.oheuropa.android.domain

import com.oheuropa.android.BuildConfig

/**
 *
 * Class: com.oheuropa.android.domain.Constants
 * Project: OhEuropa-Android
 * Created Date: 20/03/2018 14:03
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */

const val SPLASH_WAIT_SECONDS = 2

const val DEFAULT_MAP_ZOOM = 15f
const val FADE_DURATION_MS = 5000.toLong()
const val RADIO_STREAM_URL = "https://streams.radio.co/s02776f249/listen"
const val MAX_VOLUME = 100.toDouble()
const val MIN_VOLUME_INTERVAL_MS = 250

const val REQUEST_CHECK_SETTINGS = 667
const val REQUEST_PERMISSIONS = 668
const val REQUEST_PLAY_SERVICES = 669

const val TRACK_NAME_UPDATE_DELAY_MS = 10000L

const val VOL_MAX_STATIC = 15
const val VOL_MAX_RADIO = 75
const val VOL_MIN_RADIO = 25
const val VOL_MIN = 0

const val MAP_ZOOM_DURATION_SECONDS = 3

//Debug constants - to only have an effect on debug builds - by using Constants.isDebug(*)
const val USE_MOCK_BEACON_LOCATIONS = true
const val USE_MOCK_USER_LOCATION = true
const val USE_MOCK_COMPASS_READINGS = false
const val USE_MOCK_INTERACTION_EVENTS = true
const val LOG_HTTP = true

class Constants{
	companion object {
		fun isDebug(constant:Boolean):Boolean{
			return constant && BuildConfig.DEBUG
		}
	}
}
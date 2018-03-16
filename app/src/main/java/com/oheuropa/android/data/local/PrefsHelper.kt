package com.oheuropa.android.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.content.edit
import com.google.android.gms.maps.model.LatLng
import com.oheuropa.android.model.Coordinate
import javax.inject.Inject

/**
 *
 * Class: com.oheuropa.android.data.local.PrefsHelper
 * Project: OhEuropa-Android
 * Created Date: 15/03/2018 14:15
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
const val DEFAULT_ZOOM = 15f

class PrefsHelper @Inject constructor(ctx: Context) {
	private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)

	private val MAP_CENTRE_LAT = "MapCentreLat"
	private val MAP_CENTRE_LON = "MapCentreLon"
	private val MAP_ZOOM = "MapZoom"

	fun saveMapCentre(centre: LatLng?, zoom: Float?) {
		prefs.edit {
			if (centre != null) {
				putFloat(MAP_CENTRE_LAT, centre.latitude.toFloat())
				putFloat(MAP_CENTRE_LON, centre.longitude.toFloat())
			}
			if (zoom != null)
				putFloat(MAP_ZOOM, zoom)
		}
	}

	fun restoreMapCentre(): Pair<Coordinate, Float> {
		val zoom = prefs.getFloat(MAP_ZOOM, DEFAULT_ZOOM)
		val centre = Coordinate(prefs.getFloat(MAP_CENTRE_LAT, 0f), prefs.getFloat(MAP_CENTRE_LON, 0f))
		return Pair(centre, zoom)
	}
}


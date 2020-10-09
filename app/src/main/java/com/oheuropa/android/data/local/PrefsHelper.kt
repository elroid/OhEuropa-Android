package com.oheuropa.android.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.github.ajalt.timberkt.d
import com.oheuropa.android.domain.DEFAULT_MAP_ZOOM
import com.oheuropa.android.domain.MAX_UPDATE_INTERVAL_HOURS
import com.oheuropa.android.model.Coordinate
import com.oheuropa.android.ui.map.MapPresenter

/**
 *
 * Class: com.oheuropa.android.data.local.PrefsHelper
 * Project: OhEuropa-Android
 * Created Date: 15/03/2018 14:15
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
const val USER_ID = "UserId"
const val LAST_UPDATE = "LastUpdate"

class PrefsHelper constructor(ctx: Context) {
	private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)


	//map centre is stored in memory only
	private var centre: Coordinate? = null
	private var zoom: Float? = null
	private var state: MapPresenter.MapState? = null
	fun saveMapCentre(centre: Coordinate?, zoom: Float?, state: MapPresenter.MapState?) {
		this.centre = centre
		this.zoom = zoom
		this.state = state
	}

	fun restoreMapCentre(): Triple<Coordinate, Float, MapPresenter.MapState> {
		val zoom = this.zoom ?: DEFAULT_MAP_ZOOM
		val centre = this.centre ?: Coordinate(0f, 0f)
		val state = this.state ?: MapPresenter.MapState.FOCUS_HERE
		return Triple(centre, zoom, state)
	}

	fun getUserId(): String? {
		return prefs.getString(USER_ID, null)
	}

	fun hasUserId() = prefs.contains(USER_ID)

	fun setUserId(userId: String) {
		d { "setUserId:$userId" }
		prefs.edit { putString(USER_ID, userId) }
	}

	fun logUpdateSuccessful() {
		prefs.edit { putLong(LAST_UPDATE, System.currentTimeMillis()) }
	}

	fun isUpdateNeeded(): Boolean {
		val lastUpdate = prefs.getLong(LAST_UPDATE, 0)
		val maxIntervalMs = MAX_UPDATE_INTERVAL_HOURS * 3_600_000
		return (lastUpdate + maxIntervalMs) < System.currentTimeMillis()
	}
}


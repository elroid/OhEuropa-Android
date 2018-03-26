package com.oheuropa.android.ui.base

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.w
import com.google.android.gms.common.api.ResolvableApiException
import com.oheuropa.android.R
import com.oheuropa.android.data.local.AudioService
import com.oheuropa.android.domain.REQUEST_CHECK_SETTINGS
import com.oheuropa.android.domain.REQUEST_PERMISSIONS

/**
 *
 * Class: com.oheuropa.android.ui.base.BaseActivity
 * Project: OhEuropa
 * Created Date: 19/02/2018 19:04
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
abstract class LocationEnabledActivity<Pres : LocationEnabledPres>
	: BottomNavActivity(), LocationEnabledView {

	abstract var presenter: Pres

	override fun onResume() {
		super.onResume()
		presenter.start()
	}

	override fun onPause() {
		presenter.stop()
		super.onPause()
	}

	override fun isLocationPermissionGranted(): Boolean {
		return ContextCompat.checkSelfPermission(this,
			Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
	}

	override fun requestLocationPermission() {
		ActivityCompat.requestPermissions(this,
			arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSIONS)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		d { "onActivityResult($requestCode, $resultCode, $data)" }
		if (requestCode == REQUEST_CHECK_SETTINGS) {
			presenter.start()//try again regardless of result
		} else if (requestCode == REQUEST_PERMISSIONS) {
			if (resultCode == Activity.RESULT_OK)
				presenter.start()
			else
				showLocationPermissionExplanation()
		} else
			super.onActivityResult(requestCode, resultCode, data)
	}

	override fun showLocationPermissionExplanation() {
		android.app.AlertDialog.Builder(getCtx())
			.setMessage(R.string.loc_justification)
			.setPositiveButton(R.string.loc_ok) { _, _ -> requestLocationPermission() }
			.setNegativeButton(R.string.loc_quit) { _, _ -> quit() }
			.create().show()
	}

	override fun resolveApiIssue(ex: ResolvableApiException) {
		try {
			// Show the dialog by calling startResolutionForResult(),
			// and check the result in onActivityResult().
			ex.startResolutionForResult(this@LocationEnabledActivity, REQUEST_CHECK_SETTINGS)
		} catch (sendEx: IntentSender.SendIntentException) {
			w { "Send intent exception" }// Ignore the error.
		}
	}

	private var audioConnection: AudioService.Companion.AudioConnection? = null
	override fun startAudioService() {
		audioConnection = AudioService.bindService(this)
	}

	override fun onStop() {
		audioConnection?.unbind(this)
		super.onStop()
	}
}
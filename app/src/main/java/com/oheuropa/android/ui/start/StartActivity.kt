package com.oheuropa.android.ui.start

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.v
import com.github.ajalt.timberkt.w
import com.github.ajalt.timberkt.wtf
import com.google.android.gms.common.ConnectionResult.SUCCESS
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE
import com.oheuropa.android.BuildConfig
import com.oheuropa.android.R
import com.oheuropa.android.data.local.AnalyticsHelper
import com.oheuropa.android.domain.REQUEST_PLAY_SERVICES
import com.oheuropa.android.ui.base.BaseActivity
import com.oheuropa.android.ui.compass.CompassActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_start.*
import javax.inject.Inject

/**
 *
 * Class: com.oheuropa.android.ui.start.StartActivity
 * Project: OhEuropa
 * Created Date: 19/02/2018 19:03
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class StartActivity : BaseActivity(), StartContract.View {

	@Inject lateinit var presenter: StartContract.Presenter

	@SuppressLint("SetTextI18n")
	override fun onCreate(savedInstanceState: Bundle?) {
		AndroidInjection.inject(this)
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_start)
		version.text = "v${getVersion(BuildConfig.DEBUG)}"
	}

	override fun onResume() {
		super.onResume()
		presenter.start()
	}

	override fun onPause() {
		presenter.stop()
		super.onPause()
	}

	override fun continueToFirstActivity() {
		d { "continueToFirstActivity" }
		startActivity(CompassActivity.createIntent(getCtx()))
//		startActivity(MapActivity.createIntent(getCtx()))
//		startActivity(InfoActivity.createIntent(getCtx()))
//		startActivity(TestKotlinActivity.createIntent(getCtx()))
		finish()
	}

	override fun showConnectionError(msg: String?) {
		val explanation = if (msg == null) " ($msg)" else ""
		showError(msg = getString(R.string.err_beacon_conn, explanation), fatal = true)
	}

	override fun ensurePlayServicesAvailable(): Boolean {
		val apiAvailability = GoogleApiAvailability.getInstance()
		val availability = apiAvailability.isGooglePlayServicesAvailable(this)
		return when {
			availability == SUCCESS -> {
				v { "google play services are up-to-date, v:$GOOGLE_PLAY_SERVICES_VERSION_CODE" }
				true
			}
			apiAvailability.isUserResolvableError(availability) -> {
				w { "user-recoverable error" }
				AnalyticsHelper.logPlayUpdateRequired()
				apiAvailability.getErrorDialog(this, availability, REQUEST_PLAY_SERVICES).show()
				false
			}
			else -> {
				wtf { "Unrecoverable error for google play services....ignore?" }
				AnalyticsHelper.logException(message = "Unrecoverable error for google play services")
				true
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		d { "onActivityResult($requestCode, $resultCode, $data)" }
		if (requestCode == REQUEST_PLAY_SERVICES) {
			d { "result is $resultCode - doing nothing (presenter starts onResume anyway)..." }
		} else
			super.onActivityResult(requestCode, resultCode, data)
	}

	private fun getVersion(full: Boolean): String {
		return try {
			val packageInfo = packageManager.getPackageInfo(packageName, 0)
			var result = packageInfo.versionName
			if (full) result += " (" + packageInfo.versionCode + ")"
			result
		} catch (ex: Exception) {
			w(ex) { "Unable to get version" }
			"--"
		}
	}
}
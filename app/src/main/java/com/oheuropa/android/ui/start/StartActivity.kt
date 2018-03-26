package com.oheuropa.android.ui.start

import android.annotation.SuppressLint
import android.os.Bundle
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.w
import com.oheuropa.android.BuildConfig
import com.oheuropa.android.R
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
		finish()
	}

	override fun showConnectionError(msg: String?) {
		val explanation = if (msg == null) " ($msg)" else ""
		showError(msg = getString(R.string.err_beacon_conn, explanation), fatal = true)
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
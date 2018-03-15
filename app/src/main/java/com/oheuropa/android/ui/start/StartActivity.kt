package com.oheuropa.android.ui.start

import android.annotation.SuppressLint
import android.os.Bundle
import com.github.ajalt.timberkt.w
import com.oheuropa.android.BuildConfig
import com.oheuropa.android.R
import com.oheuropa.android.data.DataManager
import com.oheuropa.android.ui.base.BaseActivity
import com.oheuropa.android.ui.base.SchedulersFacade
import com.oheuropa.android.ui.map.MapActivity
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
class StartActivity : BaseActivity() {

	@Inject lateinit var dataManager: DataManager

	@SuppressLint("SetTextI18n")
	override fun onCreate(savedInstanceState: Bundle?) {
		AndroidInjection.inject(this)
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_start)
		version.text = "v${getVersion(BuildConfig.DEBUG)}"

		dataManager
			.ensureBeaconListPresent()
			.subscribeOn(SchedulersFacade.io())
			.observeOn(SchedulersFacade.ui())
			.subscribe(
				{ continueToFirstActivity() },
				{ showError(msg = getString(R.string.err_beacon_conn, it.message), fatal = true) })

	}

	private fun continueToFirstActivity() {
		//startActivity(CompassActivity.createIntent(getCtx()))
		startActivity(MapActivity.createIntent(getCtx()))
		//startActivity(InfoActivity.createIntent(getCtx()))
	}

	private fun getVersion(full: Boolean): String {
		return try {
			val pinfo = packageManager.getPackageInfo(packageName, 0)
			var result = pinfo.versionName
			if (full) result += " (" + pinfo.versionCode + ")"
			result
		} catch (ex: Exception) {
			w(ex) { "Unable to get version" }
			"--"
		}
	}
}
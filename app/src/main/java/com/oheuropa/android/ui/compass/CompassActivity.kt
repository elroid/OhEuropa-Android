package com.oheuropa.android.ui.compass

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.github.ajalt.timberkt.d
import com.oheuropa.android.R
import com.oheuropa.android.ui.base.LocationEnabledActivity
import com.oheuropa.android.util.ViewUtils
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_compass.*
import javax.inject.Inject

/**
 *
 * Class: com.oheuropa.android.ui.compass.CompassActivity
 * Project: OhEuropa
 * Created Date: 21/02/2018 17:56
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */


class CompassActivity : LocationEnabledActivity<CompassContract.Presenter>(), CompassContract.View {

	companion object {
		fun createIntent(ctx: Context): Intent {
			return Intent(ctx, CompassActivity::class.java)
		}
	}

	@Inject override lateinit var presenter: CompassContract.Presenter

	override fun onCreate(savedInstanceState: Bundle?) {
		AndroidInjection.inject(this)
		super.onCreate(savedInstanceState)

		//keep screen on while viewing the compass
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

		//ensure layout goes under status bar
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			window.decorView.systemUiVisibility =
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
				View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

		//adjust compass size to screenWidth
		val sw = ViewUtils.getScreenWidth()
		ViewUtils.setDimensions(compassFrame, sw, sw)
	}

	private var showingCompass = true
	private val COMPASS_FADE_MS = 1000
	private fun checkCompassVisibility(showCompass:Boolean){
		if(showingCompass && !showCompass){
			//hide compass
			ViewUtils.fade(false, compassView, COMPASS_FADE_MS)
		}
		else if(!showingCompass && showCompass){
			//show compass
			ViewUtils.fade(true, compassView, COMPASS_FADE_MS)
		}
		showingCompass = showCompass
	}

	override fun showNewReading(newNorthReading: Float, newBeaconReading: Float, newDistanceMeters: Int) {
		//d { "showNewReading($newNorthReading, $newBeaconReading, $newDistanceMeters)" }
		runOnUiThread {
			checkCompassVisibility(true)
			compassView.setAngles(newNorthReading, newBeaconReading)
			val dist = "" + newDistanceMeters
			statusText.text = getString(R.string.beacon_dist, dist)
		}
	}

	override fun showSongInfo(performerName: String, songTitle: String) {
		d { "showSongInfo($songTitle, $performerName" }
		runOnUiThread {
			checkCompassVisibility(false)
			statusText.text = getString(R.string.singing, performerName, songTitle)
		}
	}

	override fun getLayoutId(): Int {
		return R.layout.activity_compass
	}

	override fun getNavigationMenuItemId(): Int {
		return R.id.navigation_compass
	}
}
package com.oheuropa.android.ui.compass

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.github.ajalt.timberkt.w
import com.oheuropa.android.R
import com.oheuropa.android.ui.base.LocationEnabledActivity
import dagger.android.AndroidInjection
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
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			window.decorView.systemUiVisibility =
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
				View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

	}

	override fun displayNewReading(newBeaconReading: Float, newNorthReading: Float, newDistanceMeters: Int) {
		w { "Not implemented: displayNewReading($newBeaconReading, $newNorthReading, $newDistanceMeters)" }
	}

	override fun showCompass() {
		w { "Not implemented: showCompass()" }
	}

	override fun showSongInfo(songTitle: String, performerName: String) {
		w { "Not implemented: showSongInfo($songTitle, $performerName)" }
	}

	override fun getLayoutId(): Int {
		return R.layout.activity_compass
	}

	override fun getNavigationMenuItemId(): Int {
		return R.id.navigation_compass
	}
}
package com.oheuropa.android.ui.map

import android.content.Context
import android.content.Intent
import com.oheuropa.android.R
import com.oheuropa.android.ui.base.BottomNavActivity

/**
 *
 * Class: com.oheuropa.android.ui.compass.CompassActivity
 * Project: OhEuropa
 * Created Date: 21/02/2018 17:56
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */


class MapActivity : BottomNavActivity() {

	companion object {
		fun createIntent(ctx: Context): Intent {
			return Intent(ctx, MapActivity::class.java)
		}
	}

	override fun getLayoutId(): Int {
		return R.layout.activity_map
	}

	override fun getNavigationMenuItemId(): Int {
		return R.id.navigation_map
	}
}
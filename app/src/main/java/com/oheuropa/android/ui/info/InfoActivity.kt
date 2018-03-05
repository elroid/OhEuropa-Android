package com.oheuropa.android.ui.info

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.oheuropa.android.R
import com.oheuropa.android.ui.base.BaseActivity
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


class InfoActivity : BottomNavActivity() {

	companion object {
		fun createIntent(ctx: Context): Intent {
			return Intent(ctx, InfoActivity::class.java)
		}
	}

	override fun getLayoutId(): Int {
		return R.layout.activity_info
	}

	override fun getNavigationMenuItemId(): Int {
		return R.id.navigation_info
	}
}
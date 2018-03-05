package com.oheuropa.android.ui.start

import android.os.Bundle
import com.oheuropa.android.ui.base.BaseActivity
import com.oheuropa.android.ui.compass.CompassActivity

/**
 *
 * Class: com.oheuropa.android.ui.start.StartActivity
 * Project: OhEuropa
 * Created Date: 19/02/2018 19:03
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class StartActivity : BaseActivity(){
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		toast("Started!")

		startActivity(CompassActivity.createIntent(getCtx(), 123))
	}
}
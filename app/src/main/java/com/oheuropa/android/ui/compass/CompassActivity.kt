package com.oheuropa.android.ui.compass

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.oheuropa.android.ui.base.BaseActivity

/**
 *
 * Class: com.oheuropa.android.ui.compass.CompassActivity
 * Project: OhEuropa
 * Created Date: 21/02/2018 17:56
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */


class CompassActivity:BaseActivity() {

	companion object {
		const val MY_ID = "MyId"
		fun createIntent(ctx: Context, id : Int): Intent {
			val i = Intent(ctx, CompassActivity::class.java)
			i.putExtra(MY_ID, id)
			return i
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val id = intent.getIntExtra(MY_ID, -1)

		//toast(msg="We've arrived with id:"+id,length = LENGTH_LONG)
		toast("We've arrived with id:"+id)
	}
}
package com.oheuropa.android.ui.base

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

/**
 *
 * Class: com.oheuropa.android.ui.base.BaseActivity
 * Project: OhEuropa
 * Created Date: 19/02/2018 19:04
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
abstract class BaseActivity : AppCompatActivity() {

	protected fun getCtx(): Context {
		return this
	}

	protected fun toast(msg: CharSequence, length: Int = Toast.LENGTH_SHORT) {
		Toast.makeText(getCtx(), msg, length).show()
	}
}
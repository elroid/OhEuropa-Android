package com.oheuropa.android.ui.base

import android.content.Context
import android.widget.Toast

/**
 *
 * Class: com.oheuropa.android.ui.base.BaseView
 * Project: OhEuropa-Android
 * Created Date: 14/03/2018 13:20
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
interface BaseView {
	fun getCtx(): Context
	fun toast(msg: CharSequence, length: Int = Toast.LENGTH_SHORT)
	fun showError(msgId: Int = 0, msg: String?, fatal: Boolean = false)
	fun quit()
}
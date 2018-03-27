package com.oheuropa.android.ui.start

import com.oheuropa.android.ui.base.BasePres
import com.oheuropa.android.ui.base.BaseView

/**
 * Created Date: 26/03/2018 19:10
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
interface StartContract {

	interface View : BaseView {
		fun continueToFirstActivity()
		fun showConnectionError(msg: String?)
		fun ensurePlayServicesAvailable():Boolean
	}

	interface Presenter : BasePres
}
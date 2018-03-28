package com.oheuropa.android.ui.base

import com.google.android.gms.common.api.ResolvableApiException

/**
 *
 * Class: com.oheuropa.android.ui.base.BasePres
 * Project: OhEuropa-Android
 * Created Date: 15/03/2018 19:25
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
interface LocationEnabledPres : BasePres {

	fun onApiError(ex: ResolvableApiException)

	fun onError(ex: Exception)
}
package com.oheuropa.android.domain

import io.reactivex.Flowable

/**
 *
 * Class: com.oheuropa.android.domain.CompassProvider
 * Project: OhEuropa-Android
 * Created Date: 07/03/2018 15:58
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
interface CompassComponent {

	fun listenToCompass(): Flowable<Float>
}
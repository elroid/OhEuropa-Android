package com.oheuropa.android.ui.base

import io.reactivex.disposables.Disposable

/**
 *
 * Class: com.oheuropa.android.ui.base.BasePres
 * Project: OhEuropa-Android
 * Created Date: 16/03/2018 14:54
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
interface BasePres {
	fun start()
	fun stop()
	fun addDisposable(disposable: Disposable):Disposable
}
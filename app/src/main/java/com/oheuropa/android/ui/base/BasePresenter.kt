package com.oheuropa.android.ui.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 *
 * Class: com.oheuropa.android.ui.base.BasePreenter
 * Project: OhEuropa-Android
 * Created Date: 14/03/2018 12:54
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
abstract class BasePresenter<out V> constructor(val view:V){

	/*enum class RequestState {
		IDLE,
		LOADING,
		COMPLETE,
		ERROR
	}*/

	private val disposables = CompositeDisposable()

	/**
	 * Contains common cleanup actions needed for all presenters, if any.
	 * Subclasses may override this.
	 */
	open fun stop() {
		disposables.clear()
	}

	protected fun addDisposable(disposable: Disposable) {
		disposables.add(disposable)
	}
}
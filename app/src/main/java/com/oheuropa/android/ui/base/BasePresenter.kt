package com.oheuropa.android.ui.base

import com.github.ajalt.timberkt.d
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 *
 * Class: com.oheuropa.android.ui.base.BasePresenter
 * Project: OhEuropa-Android
 * Created Date: 14/03/2018 12:54
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
abstract class BasePresenter<out V : BaseView> constructor(val view: V) : BasePres {

	/*enum class RequestState {
		IDLE,
		LOADING,
		COMPLETE,
		ERROR
	}*/

	private val disposables = CompositeDisposable()

	abstract override fun start()
	/**
	 * Contains common cleanup actions needed for all presenters, if any.
	 * Subclasses may override this.
	 */
	override fun stop() {
		d { "stop(): clearing disposables" }
		disposables.clear()
	}

	override fun addDisposable(disposable: Disposable) {
		d { "addDisposable($disposable)" }
		disposables.add(disposable)
	}
}
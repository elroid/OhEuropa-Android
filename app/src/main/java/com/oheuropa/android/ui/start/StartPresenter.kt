package com.oheuropa.android.ui.start

import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.i
import com.oheuropa.android.data.DataManager
import com.oheuropa.android.domain.SPLASH_WAIT_SECONDS
import com.oheuropa.android.ui.base.BasePresenter
import com.oheuropa.android.ui.base.SchedulersFacade
import com.oheuropa.android.util.ViewUtils

/**
 * Created Date: 26/03/2018.
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class StartPresenter(
	startView: StartContract.View,
	private val dataManager: DataManager
) : BasePresenter<StartContract.View>(startView), StartContract.Presenter {

	override fun start() {
		i { "Starting... " }
		val start = System.currentTimeMillis()
		val beaconInit = dataManager.ensureBeaconListPresent()
		val userIdInit = dataManager.ensureUserIdCreated()

		addDisposable(beaconInit
			.andThen(userIdInit)
			.subscribeOn(SchedulersFacade.io())
			.observeOn(SchedulersFacade.ui())
			.subscribe({
				if(view.ensurePlayServicesAvailable())
					continueAfter(SPLASH_WAIT_SECONDS, start)
			}, {
				view.showConnectionError(it.message)
			}))
	}

	private fun continueAfter(secondsToWait: Int, start: Long) {
		d { "continueAfter($secondsToWait: $start)" }
		val elapsed = System.currentTimeMillis() - start
		val timeLeft = Math.max(0, secondsToWait * 1000 - elapsed)
		ViewUtils.handler().postDelayed({ view.continueToFirstActivity() }, timeLeft)
	}
}
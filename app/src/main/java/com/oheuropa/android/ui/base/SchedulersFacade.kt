package com.oheuropa.android.ui.base

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers



/**
 *
 * Class: com.oheuropa.android.ui.base.SchedulersFacade
 * Project: OhEuropa-Android
 * Created Date: 14/03/2018 13:13
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class SchedulersFacade {

	companion object {
		/**
		 * IO thread pool scheduler
		 */
		fun io(): Scheduler {
			return Schedulers.io()
		}

		/**
		 * Computation thread pool scheduler
		 */
		fun computation(): Scheduler {
			return Schedulers.computation()
		}

		/**
		 * Main Thread scheduler
		 */
		fun ui(): Scheduler {
			return AndroidSchedulers.mainThread()
		}
	}
}
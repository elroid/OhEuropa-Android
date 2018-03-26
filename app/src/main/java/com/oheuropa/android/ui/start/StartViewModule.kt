package com.oheuropa.android.ui.start

import dagger.Binds
import dagger.Module

/**
 * Created Date: 26/03/2018 19:10
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@Suppress("unused")
@Module
abstract class StartViewModule {

	@Binds
	abstract fun provideStartView(mapActivity: StartActivity): StartContract.View
}